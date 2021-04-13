/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for {@link ProjectGenerationController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectGenerationControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void simpleZipProject() {
		ResponseEntity<byte[]> entity = downloadArchive("/starter.zip?dependencies=web&dependencies=jpa");
		assertArchiveResponseHeaders(entity, MediaType.valueOf("application/zip"), "demo.zip");
		ProjectStructure project = projectFromArchive(entity.getBody());
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(3).hasDependency(Dependency.createSpringBootStarter("web"))
				// alias: jpa -> data-jpa
				.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));

	}

	@Test
	void simpleTgzProject() {
		ResponseEntity<byte[]> entity = downloadArchive("/starter.tgz?dependencies=org.acme:foo");
		assertArchiveResponseHeaders(entity, MediaType.valueOf("application/x-compress"), "demo.tar.gz");
		ProjectStructure project = tgzProjectAssert(entity.getBody());
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).doesNotContainDirectories("src/main/resources/templates", "src/main/resources/static");
		assertThat(project).mavenBuild().hasDependenciesSize(2).hasDependency("org.acme", "foo", "1.3.5");
	}

	@Test
	void tgzProjectWithLongFilenames() {
		ResponseEntity<byte[]> entity = downloadArchive(
				"/starter.tgz?name=spring-boot-service&artifactId=spring-boot-service"
						+ "&groupId=com.spring.boot.service&baseDir=spring-boot-service");
		assertArchiveResponseHeaders(entity, MediaType.valueOf("application/x-compress"), "spring-boot-service.tar.gz");
		ProjectStructure project = tgzProjectAssert(entity.getBody());
		assertThat(project).containsFiles(
				"spring-boot-service/src/test/java/com/spring/boot/service/springbootservice/SpringBootServiceApplicationTests.java");
	}

	private void assertArchiveResponseHeaders(ResponseEntity<byte[]> entity, MediaType contentType, String fileName) {
		assertThat(entity.getHeaders().getContentType()).isEqualTo(contentType);
		assertThat(entity.getHeaders().getContentDisposition()).isNotNull();
		assertThat(entity.getHeaders().getContentDisposition().getFilename()).isEqualTo(fileName);
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
	void dependencyNotInRange() {
		try {
			execute("/starter.tgz?dependencies=org.acme:bur", byte[].class, null, (String[]) null);
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Test
	void noDependencyProject() {
		ProjectStructure project = downloadZip("/starter.zip");
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(2)
				// the root dep is added if none is specified
				.hasDependency(Dependency.createSpringBootStarter(""))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void dependencies() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&dependencies=jpa");
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(3).hasDependency(Dependency.createSpringBootStarter("web"))
				// alias: jpa -> data-jpa
				.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void dependenciesCommaSeparated() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web,jpa");
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(3).hasDependency(Dependency.createSpringBootStarter("web"))
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
		assertThat(project).mavenBuild().hasDependenciesSize(4).hasProperty("kotlin.version", "1.4.31");
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
		assertThatExceptionOfType(HttpClientErrorException.class)
				.isThrownBy(() -> downloadArchive("/starter.zip?dependencies=foo:bar")).satisfies((ex) -> {
					assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
					assertStandardErrorBody(ex.getResponseBodyAsString(),
							"Unknown dependency 'foo:bar' check project metadata");
				});
	}

	@Test
	void invalidDependencyProperException() {
		assertThatExceptionOfType(HttpClientErrorException.class)
				.isThrownBy(() -> downloadArchive("/starter.zip?dependencies=foo")).satisfies((ex) -> {
					assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
					assertStandardErrorBody(ex.getResponseBodyAsString(),
							"Unknown dependency 'foo' check project metadata");
				});
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
		assertThatExceptionOfType(HttpClientErrorException.class)
				.isThrownBy(() -> getRestTemplate().getForEntity(createUrl(url), byte[].class)).satisfies((ex) -> {
					assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
					assertStandardErrorBody(ex.getResponseBodyAsString(),
							"Dependencies must be specified using 'dependencies'");
				});
	}

	@Test
	void webIsAddedPom() {
		String body = getRestTemplate().getForObject(createUrl("/pom.xml?packaging=war"), String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("provided");
	}

	@Test
	void webIsAddedGradle() {
		String body = getRestTemplate().getForObject(createUrl("/build.gradle?packaging=war"), String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("providedRuntime");
	}

	@Test
	void downloadStarter() {
		byte[] body = getRestTemplate().getForObject(createUrl("starter.zip"), byte[].class);
		assertThat(body).isNotNull();
		assertThat(body.length).isGreaterThan(100);
	}

	@Test
	void curlCanStillDownloadZipArchive() {
		ResponseEntity<byte[]> response = execute("/starter.zip", byte[].class, "curl/1.2.4", "*/*");
		assertDefaultProject(projectFromArchive(response.getBody()));
	}

	@Test
	void curlCanStillDownloadTgzArchive() {
		ResponseEntity<byte[]> response = execute("/starter.tgz", byte[].class, "curl/1.2.4", "*/*");
		assertDefaultProject(tgzProjectAssert(response.getBody()));
	}

	private static void assertStandardErrorBody(String body, String message) {
		assertThat(body).as("error body must be available").isNotNull();
		try {
			JSONObject model = new JSONObject(body);
			assertThat(model.get("message")).isEqualTo(message);
		}
		catch (JSONException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

}
