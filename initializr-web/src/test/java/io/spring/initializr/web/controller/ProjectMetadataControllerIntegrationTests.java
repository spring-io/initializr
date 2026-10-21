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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectMetadataController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectMetadataControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void metadataWithNoAcceptHeader() {
		// HTTP clients typically send '*/*' when no Accept header is specified
		MvcTestResult response = invokeHome(null, "*/*");
		validateDefaultMetadata(response);
	}

	@Test
	@Disabled("Need a comparator that does not care about the number of elements in an array")
	void currentMetadataCompatibleWithV2() {
		MvcTestResult response = invokeHome(null, "*/*");
		validateMetadata(response, AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE, "2.0.0",
				JSONCompareMode.LENIENT);
	}

	@Test
	void metadataWithV2AcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV21AcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2.1+json");
		validateMetadata(response, InitializrMetadataVersion.V2_1.getMediaType(), "2.1.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV22AcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2.2+json");
		validateMetadata(response, InitializrMetadataVersion.V2_2.getMediaType(), "2.2.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithV23AcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2.3+json");
		validateMetadata(response, InitializrMetadataVersion.V2_3.getMediaType(), "2.3.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithInvalidPlatformVersion() {
		assertBadRequest(execute("/dependencies?bootVersion=2.2.17.RELEASE", null,
				"application/vnd.initializr.v2.1+json", "application/json"), "2.2.17.RELEASE");
	}

	@Test
	void dependenciesWithWildcardPlatformVersion() {
		MvcTestResult response = execute("/dependencies?bootVersion=2.4.x", null,
				"application/vnd.initializr.v2.1+json");
		assertThat(bodyOf(response)).contains("\"bootVersion\":\"2.4.4\"");
	}

	@Test
	void dependenciesWithMajorOnlyPlatformVersion() {
		MvcTestResult response = execute("/dependencies?bootVersion=2", null, "application/vnd.initializr.v2.1+json");
		assertThat(bodyOf(response)).contains("\"bootVersion\":\"2.4.4\"");
	}

	@ParameterizedTest
	@ValueSource(strings = { "9.9.x", "2.x.3", "2.4.x-M1" })
	void dependenciesWithPlatformVersionThatCannotBeResolved(String bootVersion) {
		assertBadRequest(
				execute("/dependencies?bootVersion=" + bootVersion, null, "application/vnd.initializr.v2.1+json"),
				bootVersion);
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2.3+json");
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionsAndQualifier() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2+json;q=0.9",
				"application/vnd.initializr.v2.3+json");
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionAndPreferenceOnInvalidVersion() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v5.4+json",
				"application/vnd.initializr.v2.3+json;q=0.9");
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionAndPreferenceForOldVersion() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2+json",
				"application/vnd.initializr.v2.2+json;q=0.9");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0", JSONCompareMode.STRICT);
	}

	@Test
	void metadataWithHalAcceptHeader() {
		MvcTestResult response = invokeHome(null, "application/hal+json");
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		validateContentType(response, ProjectMetadataController.HAL_JSON_CONTENT_TYPE);
		validateDefaultMetadata(bodyOf(response));
	}

	@Test
	void metadataWithUnknownAcceptHeader() {
		assertThat(invokeHome(null, "application/vnd.initializr.v5.4+json")).hasStatus(HttpStatus.NOT_ACCEPTABLE);
	}

	@Test
	void homeIsJson() {
		String body = bodyOf(invokeHome(null, (String[]) null));
		assertThat(body).contains("\"dependencies\"");
	}

	@Test
	void unknownAgentReceivesJsonByDefault() {
		MvcTestResult response = invokeHome("foo/1.0", "*/*");
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
		return bodyOf(invokeHome(userAgentHeader, acceptHeaders));
	}

}
