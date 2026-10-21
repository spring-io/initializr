/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectGenerationController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectGenerationControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void simpleZipProject() {
		MvcTestResult result = downloadArchive("/starter.zip?dependencies=web&dependencies=jpa");
		assertArchiveResponseHeaders(result, MediaType.valueOf("application/zip"), "demo.zip");
		byte[] body = result.getResponse().getContentAsByteArray();
		ProjectStructure project = projectFromArchive(body);
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(3)
			.hasDependency(Dependency.createSpringBootStarter("web"))
			// alias: jpa -> data-jpa
			.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
		// default configuration file format is .properties
		assertThat(project).containsFiles("src/main/resources/application.properties");
	}

	@Test
	void simpleZipProjectWithPropertiesFormat() {
		MvcTestResult result = downloadArchive("/starter.zip?dependencies=web&configurationFileFormat=properties");
		assertArchiveResponseHeaders(result, MediaType.valueOf("application/zip"), "demo.zip");
		byte[] body = result.getResponse().getContentAsByteArray();
		ProjectStructure project = projectFromArchive(body);
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(2)
			.hasDependency(Dependency.createSpringBootStarter("web"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
		assertThat(project).containsFiles("src/main/resources/application.properties");
	}

	@Test
	void simpleZipProjectWithYAMLFormat() {
		MvcTestResult result = downloadArchive("/starter.zip?dependencies=web&configurationFileFormat=yaml");
		assertArchiveResponseHeaders(result, MediaType.valueOf("application/zip"), "demo.zip");
		byte[] body = result.getResponse().getContentAsByteArray();
		ProjectStructure project = projectFromArchive(body);
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(2)
			.hasDependency(Dependency.createSpringBootStarter("web"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
		assertThat(project).containsFiles("src/main/resources/application.yaml");
	}

	@Test
	void simpleTgzProject() {
		MvcTestResult result = downloadArchive("/starter.tgz?dependencies=org.acme:foo");
		assertArchiveResponseHeaders(result, MediaType.valueOf("application/x-compress"), "demo.tar.gz");
		byte[] body = result.getResponse().getContentAsByteArray();
		ProjectStructure project = tgzProjectAssert(body);
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).doesNotContainDirectories("src/main/resources/templates", "src/main/resources/static");
		assertThat(project).mavenBuild().hasDependenciesSize(2).hasDependency("org.acme", "foo", "1.3.5");
	}

	@Test
	void tgzProjectWithLongFilenames() {
		MvcTestResult result = downloadArchive("/starter.tgz?name=spring-boot-service&artifactId=spring-boot-service"
				+ "&groupId=com.spring.boot.service&baseDir=spring-boot-service");
		assertArchiveResponseHeaders(result, MediaType.valueOf("application/x-compress"), "spring-boot-service.tar.gz");
		byte[] body = result.getResponse().getContentAsByteArray();
		ProjectStructure project = tgzProjectAssert(body);
		assertThat(project).containsFiles(
				"spring-boot-service/src/test/java/com/spring/boot/service/spring_boot_service/SpringBootServiceApplicationTests.java");
	}

	private void assertArchiveResponseHeaders(MvcTestResult result, MediaType contentType, String fileName) {
		assertThat(result).hasStatusOk().hasContentType(contentType);
		String contentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertThat(contentDisposition).isNotNull();
		assertThat(ContentDisposition.parse(contentDisposition).getFilename()).isEqualTo(fileName);
	}

	@Test
	void dependencyInRange() {
		Dependency biz = Dependency.create("org.acme", "biz", "1.3.5", "runtime");
		ProjectStructure project = downloadTgz("/starter.tgz?dependencies=org.acme:biz&bootVersion=2.6.1");
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(3).hasDependency(biz);
	}

	@Test
	void majorOnlyPlatformVersionIsResolved() {
		ProjectStructure project = downloadTgz("/starter.tgz?bootVersion=2");
		assertThat(project).mavenBuild().hasParent("org.springframework.boot", "spring-boot-starter-parent", "2.4.4");
	}

	@Test
	void wildcardPlatformVersionIsResolved() {
		ProjectStructure project = downloadTgz("/starter.tgz?bootVersion=2.3.x");
		assertThat(project).mavenBuild()
			.hasParent("org.springframework.boot", "spring-boot-starter-parent", "2.3.10.RELEASE");
	}

	@Test
	void wildcardPlatformVersionThatDoesNotMatchIsRejected() {
		assertThat(downloadArchive("/starter.tgz?bootVersion=9.9.x")).hasStatus(HttpStatus.BAD_REQUEST);
	}

	@Test
	void dependencyNotInRange() {
		assertBadRequest(downloadArchive("/starter.tgz?dependencies=org.acme:bur&bootVersion=2.3.10.RELEASE"),
				"Dependency 'org.acme:bur' is not compatible with Spring Boot 2.3.10.RELEASE");
	}

	@Test
	void noDependencyProject() {
		ProjectStructure project = downloadZip("/starter.zip");
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(2)
			// the root dep is added if none is specified
			.hasDependency(Dependency.createSpringBootStarter(""))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void dependencies() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&dependencies=jpa");
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(3)
			.hasDependency(Dependency.createSpringBootStarter("web"))
			// alias: jpa -> data-jpa
			.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void dependenciesCommaSeparated() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web,jpa");
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(3)
			.hasDependency(Dependency.createSpringBootStarter("web"))
			// alias: jpa -> data-jpa
			.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void kotlinRange() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&language=kotlin&bootVersion=2.4.0");
		assertThat(project).containsFiles("src/main/kotlin/com/example/demo/DemoApplication.kt",
				"src/test/kotlin/com/example/demo/DemoApplicationTests.kt",
				"src/main/resources/application.properties");
		assertThat(project).mavenBuild().hasDependenciesSize(5).hasProperty("kotlin.version", "1.4.31");
	}

	@Test
	void gradleWarProject() {
		ProjectStructure project = downloadZip(
				"/starter.zip?dependencies=web&dependencies=security&packaging=war&type=gradle-project");
		assertThat(project).hasGroovyDslGradleBuild().hasGradleWrapper();
		assertThat(project).containsFiles("src/main/java/com/example/demo/DemoApplication.java",
				"src/main/java/com/example/demo/ServletInitializer.java",
				"src/test/java/com/example/demo/DemoApplicationTests.java",
				"src/main/resources/application.properties");
		assertHasWebResources(project);
	}

	@Test
	void missingDependencyProperException() {
		assertBadRequest(downloadArchive("/starter.zip?dependencies=foo:bar"),
				"Unknown dependency 'foo:bar' check project metadata");
	}

	@Test
	void invalidDependencyProperException() {
		assertBadRequest(downloadArchive("/starter.zip?dependencies=foo"),
				"Unknown dependency 'foo' check project metadata");
	}

	@Test
	void styleWithProjectZip() {
		assertUsingStyleIsFailingForUrl("/starter.zip?dependencies=web&style=should-not-use");
	}

	@Test
	void styleWithProjectTgz() {
		assertUsingStyleIsFailingForUrl("/starter.tgz?dependencies=web&style=should-not-use");
	}

	@Test
	void styleWithMavenBuild() {
		assertUsingStyleIsFailingForUrl("/pom.xml?dependencies=web&style=should-not-use");
	}

	@Test
	void styleWithGradleBuild() {
		assertUsingStyleIsFailingForUrl("/build.gradle?dependencies=web&style=should-not-use");
	}

	private void assertUsingStyleIsFailingForUrl(String url) {
		assertBadRequest(this.mvc.get().uri(url).exchange(), "Dependencies must be specified using 'dependencies'");
	}

	@Test
	void webIsAddedPom() {
		String body = bodyOf(this.mvc.get().uri("/pom.xml?packaging=war").exchange());
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("provided");
	}

	@Test
	void webIsAddedGradle() {
		String body = bodyOf(this.mvc.get().uri("/build.gradle?packaging=war").exchange());
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("providedRuntime");
	}

	@Test
	void downloadStarter() {
		MvcTestResult result = downloadArchive("/starter.zip");
		assertThat(result).hasStatusOk();
		assertThat(result.getResponse().getContentAsByteArray().length).isGreaterThan(100);
	}

	@Test
	void curlCanStillDownloadZipArchive() {
		MvcTestResult result = execute("/starter.zip", "curl/1.2.4", "*/*");
		assertThat(result).hasStatusOk();
		assertDefaultProject(projectFromArchive(result.getResponse().getContentAsByteArray()));
	}

	@Test
	void curlCanStillDownloadTgzArchive() {
		MvcTestResult result = execute("/starter.tgz", "curl/1.2.4", "*/*");
		assertThat(result).hasStatusOk();
		assertDefaultProject(tgzProjectAssert(result.getResponse().getContentAsByteArray()));
	}

}
