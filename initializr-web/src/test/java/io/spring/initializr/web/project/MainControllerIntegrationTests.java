/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.web.project;

import java.net.URI;
import java.net.URISyntaxException;

import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
class MainControllerIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	void simpleZipProject() {
		ResponseEntity<byte[]> entity = downloadArchive(
				"/starter.zip?style=web&style=jpa");
		assertArchiveResponseHeaders(entity, MediaType.valueOf("application/zip"),
				"demo.zip");
		zipProjectAssert(entity.getBody()).isJavaProject().hasFile(".gitignore")
				.hasExecutableFile("mvnw").isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	void simpleTgzProject() {
		ResponseEntity<byte[]> entity = downloadArchive(
				"/starter.tgz?style=org.acme:foo");
		assertArchiveResponseHeaders(entity, MediaType.valueOf("application/x-compress"),
				"demo.tar.gz");
		tgzProjectAssert(entity.getBody()).isJavaProject().hasFile(".gitignore")
				.hasExecutableFile("mvnw").isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(2)
				.hasDependency("org.acme", "foo", "1.3.5");
	}

	private void assertArchiveResponseHeaders(ResponseEntity<byte[]> entity,
			MediaType contentType, String fileName) {
		assertThat(entity.getHeaders().getContentType()).isEqualTo(contentType);
		assertThat(entity.getHeaders().getContentDisposition()).isNotNull();
		assertThat(entity.getHeaders().getContentDisposition().getFilename())
				.isEqualTo(fileName);
	}

	@Test
	void dependencyInRange() {
		Dependency biz = Dependency.create("org.acme", "biz", "1.3.5", "runtime");
		downloadTgz("/starter.tgz?style=org.acme:biz&bootVersion=2.2.1.RELEASE")
				.isJavaProject().isMavenProject().hasStaticAndTemplatesResources(false)
				.pomAssert().hasDependenciesCount(3).hasDependency(biz);
	}

	@Test
	void dependencyNotInRange() {
		try {
			execute("/starter.tgz?style=org.acme:bur", byte[].class, null,
					(String[]) null);
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Test
	void noDependencyProject() {
		downloadZip("/starter.zip").isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(2)
				// the root dep is added if none is specified
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest();
	}

	@Test
	void dependenciesIsAnAliasOfStyle() {
		downloadZip("/starter.zip?dependencies=web&dependencies=jpa").isJavaProject()
				.isMavenProject().hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3).hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	void dependenciesIsAnAliasOfStyleCommaSeparated() {
		downloadZip("/starter.zip?dependencies=web,jpa").isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa") // alias jpa -> data-jpa
				.hasSpringBootStarterTest();
	}

	@Test
	void kotlinRange() {
		downloadZip("/starter.zip?style=web&language=kotlin&bootVersion=2.0.1.RELEASE")
				.isKotlinProject().isMavenProject().pomAssert().hasDependenciesCount(4)
				.hasProperty("kotlin.version", "1.1");
	}

	@Test
	void gradleWarProject() {
		downloadZip(
				"/starter.zip?style=web&style=security&packaging=war&type=gradle-project")
						.isJavaWarProject().isGradleProject();
	}

	@Test
	void downloadCli() throws Exception {
		assertSpringCliRedirect("/spring", "zip");
	}

	@Test
	void downloadCliAsZip() throws Exception {
		assertSpringCliRedirect("/spring.zip", "zip");
	}

	@Test
	void downloadCliAsTarGz() throws Exception {
		assertSpringCliRedirect("/spring.tar.gz", "tar.gz");
	}

	@Test
	void downloadCliAsTgz() throws Exception {
		assertSpringCliRedirect("/spring.tgz", "tar.gz");
	}

	private void assertSpringCliRedirect(String context, String extension)
			throws URISyntaxException {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl(context),
				ResponseEntity.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.1.4.RELEASE/spring-boot-cli-2.1.4.RELEASE-bin."
				+ extension;
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

	@Test
	void metadataWithNoAcceptHeader() {
		// rest template sets application/json by default
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateCurrentMetadata(response);
	}

	@Test
	@Disabled("Need a comparator that does not care about the number of elements in an array")
	public void currentMetadataCompatibleWithV2() {
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateMetadata(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE, "2.0.0",
				JSONCompareMode.LENIENT);
	}

	@Test
	void metadataWithV2AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null,
				"application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0",
				JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
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
	void metadataWithSeveralAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null,
				"application/vnd.initializr.v2.1+json",
				"application/vnd.initializr.v2+json");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	void metadataWithHalAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/hal+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, MainController.HAL_JSON_CONTENT_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	void metadataWithUnknownAcceptHeader() {
		try {
			invokeHome(null, "application/vnd.initializr.v5.4+json");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Test
	void curlReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		validateCurlHelpContent(response);
	}

	@Test
	void curlCanStillDownloadZipArchive() {
		ResponseEntity<byte[]> response = execute("/starter.zip", byte[].class,
				"curl/1.2.4", "*/*");
		zipProjectAssert(response.getBody()).isMavenProject().isJavaProject();
	}

	@Test
	void curlCanStillDownloadTgzArchive() {
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
	void curlWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "text/plain");
		validateCurlHelpContent(response);
	}

	@Test
	void unknownAgentReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome("foo/1.0", "*/*");
		validateCurrentMetadata(response);
	}

	@Test
	void httpieReceivesTextByDefault() {
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
	void httpieWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "text/plain");
		validateHttpIeHelpContent(response);
	}

	@Test
	void unknownCliWithTextPlain() {
		ResponseEntity<String> response = invokeHome(null, "text/plain");
		validateGenericHelpContent(response);
	}

	@Test
	void springBootCliReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome("SpringBootCli/1.2.0", "*/*");
		validateContentType(response,
				AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response.getBody());
	}

	@Test
	void springBootCliWithAcceptHeaderText() {
		ResponseEntity<String> response = invokeHome("SpringBootCli/1.2.0", "text/plain");
		validateSpringBootHelpContent(response);
	}

	@Test
	// Test that the current output is exactly what we expect
	public void validateCurrentProjectMetadata() {
		validateCurrentMetadata(getMetadataJson());
	}

	@Test
	void doNotForceSslByDefault() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		String body = response.getBody();
		assertThat(body).as("Must not force https").contains("http://start.spring.io/");
		assertThat(body).as("Must not force https").doesNotContain("https://");
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
	void missingDependencyProperException() {
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
	void invalidDependencyProperException() {
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
	void homeIsJson() {
		String body = invokeHome(null, (String[]) null).getBody();
		assertThat(body).contains("\"dependencies\"");
	}

	@Test
	void webIsAddedPom() {
		String body = getRestTemplate().getForObject(createUrl("/pom.xml?packaging=war"),
				String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("provided");
	}

	@Test
	void webIsAddedGradle() {
		String body = getRestTemplate()
				.getForObject(createUrl("/build.gradle?packaging=war"), String.class);
		assertThat(body).contains("spring-boot-starter-web");
		assertThat(body).contains("providedRuntime");
	}

	@Test
	void downloadStarter() {
		byte[] body = getRestTemplate().getForObject(createUrl("starter.zip"),
				byte[].class);
		assertThat(body).isNotNull();
		assertThat(body.length).isGreaterThan(100);
	}

	@Test
	void installer() {
		ResponseEntity<String> response = getRestTemplate()
				.getForEntity(createUrl("install.sh"), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
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
