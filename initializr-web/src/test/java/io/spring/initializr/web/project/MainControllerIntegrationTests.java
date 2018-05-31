/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.project;

import java.net.URI;
import java.net.URISyntaxException;

import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class MainControllerIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	public void simpleZipProject() {
		downloadZip("/starter.zip?style=web&style=jpa").isJavaProject()
				.hasFile(".gitignore").hasExecutableFile("mvnw").isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	public void simpleTgzProject() {
		downloadTgz("/starter.tgz?style=org.acme:foo").isJavaProject()
				.hasFile(".gitignore").hasExecutableFile("mvnw").isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(2)
				.hasDependency("org.acme", "foo", "1.3.5");
	}

	@Test
	public void dependencyInRange() {
		Dependency biz = Dependency.create("org.acme", "biz", "1.3.5", "runtime");
		downloadTgz("/starter.tgz?style=org.acme:biz&bootVersion=1.2.1.RELEASE")
				.isJavaProject().isMavenProject().hasStaticAndTemplatesResources(false)
				.pomAssert().hasDependenciesCount(2).hasDependency(biz);
	}

	@Test
	public void dependencyNotInRange() {
		try {
			execute("/starter.tgz?style=org.acme:bur", byte[].class, null,
					(String[]) null);
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Test
	public void noDependencyProject() {
		downloadZip("/starter.zip").isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(2)
				// the root dep is added if none is specified
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest();
	}

	@Test
	public void dependenciesIsAnAliasOfStyle() {
		downloadZip("/starter.zip?dependencies=web&dependencies=jpa").isJavaProject()
				.isMavenProject().hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3).hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	public void dependenciesIsAnAliasOfStyleCommaSeparated() {
		downloadZip("/starter.zip?dependencies=web,jpa").isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	public void kotlinRange() {
		downloadZip("/starter.zip?style=web&language=kotlin&bootVersion=1.2.1.RELEASE")
				.isKotlinProject().isMavenProject().pomAssert().hasDependenciesCount(4)
				.hasProperty("kotlin.version", "1.1");
	}

	@Test
	public void gradleWarProject() {
		downloadZip("/starter.zip?style=web&style=security&packaging=war&type=gradle.zip")
				.isJavaWarProject().isGradleProject();
	}

	@Test
	public void downloadCli() throws Exception {
		assertSpringCliRedirect("/spring", "zip");
	}

	@Test
	public void downloadCliAsZip() throws Exception {
		assertSpringCliRedirect("/spring.zip", "zip");
	}

	@Test
	public void downloadCliAsTarGz() throws Exception {
		assertSpringCliRedirect("/spring.tar.gz", "tar.gz");
	}

	@Test
	public void downloadCliAsTgz() throws Exception {
		assertSpringCliRedirect("/spring.tgz", "tar.gz");
	}

	private void assertSpringCliRedirect(String context, String extension)
			throws URISyntaxException {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl(context),
				ResponseEntity.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/1.1.4.RELEASE/spring-boot-cli-1.1.4.RELEASE-bin."
				+ extension;
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

	@Test
	public void metadataWithNoAcceptHeader() {
		// rest template sets application/json by default
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateCurrentMetadata(response);
	}

	@Test
	@Ignore("Need a comparator that does not care about the number of elements in an array")
	public void currentMetadataCompatibleWithV2() {
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateMetadata(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE, "2.0.0",
				JSONCompareMode.LENIENT);
	}

	@Test
	public void metadataWithV2AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null,
				"application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0",
				JSONCompareMode.STRICT);
	}

	@Test
	public void metadataWithCurrentAcceptHeader() {
		getRequests().setFields("_links.maven-project", "dependencies.values[0]",
				"type.values[0]", "javaVersion.values[0]", "packaging.values[0]",
				"bootVersion.values[0]", "language.values[0]");
		ResponseEntity<String> response = invokeHome(null,
				"application/vnd.initializr.v2.1+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void metadataWithSeveralAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null,
				"application/vnd.initializr.v2.1+json",
				"application/vnd.initializr.v2+json");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void metadataWithHalAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/hal+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, MainController.HAL_JSON_CONTENT_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void metadataWithUnknownAcceptHeader() {
		try {
			invokeHome(null, "application/vnd.initializr.v5.4+json");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Test
	public void curlReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		validateCurlHelpContent(response);
	}

	@Test
	public void curlCanStillDownloadZipArchive() {
		ResponseEntity<byte[]> response = execute("/starter.zip", byte[].class,
				"curl/1.2.4", "*/*");
		zipProjectAssert(response.getBody()).isMavenProject().isJavaProject();
	}

	@Test
	public void curlCanStillDownloadTgzArchive() {
		ResponseEntity<byte[]> response = execute("/starter.tgz", byte[].class,
				"curl/1.2.4", "*/*");
		tgzProjectAssert(response.getBody()).isMavenProject().isJavaProject();
	}

	@Test
	// make sure curl can still receive metadata with json
	public void curlWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "application/json");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void curlWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "text/plain");
		validateCurlHelpContent(response);
	}

	@Test
	public void unknownAgentReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome("foo/1.0", "*/*");
		validateCurrentMetadata(response);
	}

	@Test
	public void httpieReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "*/*");
		validateHttpIeHelpContent(response);
	}

	@Test
	// make sure curl can still receive metadata with json
	public void httpieWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "application/json");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void httpieWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "text/plain");
		validateHttpIeHelpContent(response);
	}

	@Test
	public void unknownCliWithTextPlain() {
		ResponseEntity<String> response = invokeHome(null, "text/plain");
		validateGenericHelpContent(response);
	}

	@Test
	public void springBootCliReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome("SpringBootCli/1.2.0", "*/*");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	public void springBootCliWithAcceptHeaderText() {
		ResponseEntity<String> response = invokeHome("SpringBootCli/1.2.0", "text/plain");
		validateSpringBootHelpContent(response);
	}

	@Test
	// Test that the current output is exactly what we expect
	public void validateCurrentProjectMetadata() {
		validateCurrentMetadata(getMetadataJson());
	}

	private void validateCurlHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody()).contains("Spring Initializr", "Examples:", "curl");
	}

	private void validateHttpIeHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody()).contains("Spring Initializr", "Examples:", "http")
				.doesNotContain("curl");
	}

	private void validateGenericHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody()).contains("Spring Initializr")
				.doesNotContain("Examples:", "curl");
	}

	private void validateSpringBootHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody())
				.contains("Service capabilities", "Supported dependencies")
				.doesNotContain("Examples:", "curl");
	}

	@Test
	public void missingDependencyProperException() {
		try {
			downloadArchive("/starter.zip?style=foo:bar");
			fail("Should have failed");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertStandardErrorBody(ex.getResponseBodyAsString(),
					"Unknown dependency 'foo:bar' check project metadata");
		}
	}

	@Test
	public void invalidDependencyProperException() {
		try {
			downloadArchive("/starter.zip?style=foo");
			fail("Should have failed");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertStandardErrorBody(ex.getResponseBodyAsString(),
					"Unknown dependency 'foo' check project metadata");
		}
	}

	@Test
	public void homeIsJson() {
		String body = invokeHome(null, (String[]) null).getBody();
		assertThat(body).contains("\"dependencies\"");
	}

	@Test
	public void webIsAddedPom() {
		String body = getRestTemplate().getForObject(createUrl("/pom.xml?packaging=war"),
				String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("provided");
	}

	@Test
	public void webIsAddedGradle() {
		String body = getRestTemplate()
				.getForObject(createUrl("/build.gradle?packaging=war"), String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("providedRuntime");
	}

	@Test
	public void homeHasWebStyle() {
		String body = htmlHome();
		assertThat(body).contains("name=\"style\" value=\"web\"");
	}

	@Test
	public void homeHasBootVersion() {
		String body = htmlHome();
		assertThat(body).contains("name=\"bootVersion\"");
		assertThat(body).contains("1.2.0.BUILD-SNAPSHOT\"");
	}

	@Test
	public void homeHasOnlyProjectFormatTypes() {
		String body = htmlHome();
		assertThat(body).contains("Maven Project");
		assertThat(body).doesNotContain("Maven POM");
	}

	@Test
	public void downloadStarter() {
		byte[] body = getRestTemplate().getForObject(createUrl("starter.zip"),
				byte[].class);
		assertThat(body).isNotNull();
		assertThat(body.length).isGreaterThan(100);
	}

	@Test
	public void installer() {
		ResponseEntity<String> response = getRestTemplate()
				.getForEntity(createUrl("install.sh"), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	public void googleAnalyticsDisabledByDefault() {
		String body = htmlHome();
		assertThat(body).doesNotContain("GoogleAnalyticsObject");
	}

	private String getMetadataJson() {
		return getMetadataJson(null);
	}

	private String getMetadataJson(String userAgentHeader, String... acceptHeaders) {
		return invokeHome(userAgentHeader, acceptHeaders).getBody();
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
