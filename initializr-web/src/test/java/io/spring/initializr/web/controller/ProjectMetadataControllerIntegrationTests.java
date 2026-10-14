/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for {@link ProjectMetadataController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectMetadataControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void metadataWithNoAcceptHeader() {
		// rest template sets application/json by default
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateDefaultMetadata(response);
	}

	@Test
	@Disabled("Need a comparator that does not care about the number of elements in an array")
	void currentMetadataCompatibleWithV2() {
		ResponseEntity<String> response = invokeHome(null, "*/*");
		validateMetadata(response, AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE, "2.0.0",
				JSONCompareMode.LENIENT);
	}

	@Test
	void metadataWithV2AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV21AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.1+json");
		validateMetadata(response, InitializrMetadataVersion.V2_1.getMediaType(), "2.1.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV22AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.2+json");
		validateMetadata(response, InitializrMetadataVersion.V2_2.getMediaType(), "2.2.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV23AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.3+json");
		validateMetadata(response, InitializrMetadataVersion.V2_3.getMediaType(), "2.3.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithInvalidPlatformVersion() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/dependencies?bootVersion=2.2.17.RELEASE", String.class,
					"application/vnd.initializr.v2.1+json", "application/json"))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getResponseBodyAsString()).contains("2.2.17.RELEASE");
			});
	}

	@Test
	void dependenciesWithWildcardPlatformVersion() {
		ResponseEntity<String> response = execute("/dependencies?bootVersion=2.4.x", String.class, null,
				"application/vnd.initializr.v2.1+json");
		assertThat(response.getBody()).contains("\"bootVersion\":\"2.4.4\"");
	}

	@Test
	void dependenciesWithMajorOnlyPlatformVersion() {
		ResponseEntity<String> response = execute("/dependencies?bootVersion=2", String.class, null,
				"application/vnd.initializr.v2.1+json");
		assertThat(response.getBody()).contains("\"bootVersion\":\"2.4.4\"");
	}

	@ParameterizedTest
	@ValueSource(strings = { "9.9.x", "2.x.3", "2.4.x-M1" })
	void dependenciesWithPlatformVersionThatCannotBeResolved(String bootVersion) {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/dependencies?bootVersion=" + bootVersion, String.class, null,
					"application/vnd.initializr.v2.1+json"))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getResponseBodyAsString()).contains(bootVersion);
			});
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
		getRequests().setFields("_links.maven-project", "dependencies.values[0]", "type.values[0]",
				"javaVersion.values[0]", "packaging.values[0]", "bootVersion.values[0]", "language.values[0]",
				"configurationFileFormat.values[0]");
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.3+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionsAndQualifier() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json;q=0.9",
				"application/vnd.initializr.v2.3+json");
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionAndPreferenceOnInvalidVersion() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v5.4+json",
				"application/vnd.initializr.v2.3+json;q=0.9");
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionAndPreferenceForOldVersion() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json",
				"application/vnd.initializr.v2.2+json;q=0.9");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithHalAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, "application/hal+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, ProjectMetadataController.HAL_JSON_CONTENT_TYPE);
		validateDefaultMetadata(response.getBody());
	}

	@Test
	void metadataWithUnknownAcceptHeader() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> invokeHome(null, "application/vnd.initializr.v5.4+json"))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE));
	}

	@Test
	void homeIsJson() {
		String body = invokeHome(null, (String[]) null).getBody();
		assertThat(body).contains("\"dependencies\"");
	}

	@Test
	void unknownAgentReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome("foo/1.0", "*/*");
		validateDefaultMetadata(response);
	}

	@Test
	// Test that the current output is exactly what we expect
	void validateCurrentProjectMetadata() {
		validateDefaultMetadata(getMetadataJson());
	}

	private @Nullable String getMetadataJson() {
		return getMetadataJson(null);
	}

	private @Nullable String getMetadataJson(@Nullable String userAgentHeader, String... acceptHeaders) {
		return invokeHome(userAgentHeader, acceptHeaders).getBody();
	}

}
