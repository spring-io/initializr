/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommandLineMetadataController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class CommandLineMetadataControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void curlReceivesTextByDefault() {
		MvcTestResult response = invokeHome("curl/1.2.4", "*/*");
		validateCurlHelpContent(response);
	}

	@Test
	// make sure curl can still receive metadata with json
	void curlWithAcceptHeaderJson() {
		MvcTestResult response = invokeHome("curl/1.2.4", "application/json");
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(bodyOf(response));
	}

	@Test
	void curlWithAcceptHeaderTextPlain() {
		MvcTestResult response = invokeHome("curl/1.2.4", "text/plain");
		validateCurlHelpContent(response);
	}

	@Test
	void httpieReceivesTextByDefault() {
		MvcTestResult response = invokeHome("HTTPie/0.8.0", "*/*");
		validateHttpIeHelpContent(response);
	}

	@Test
	// make sure curl can still receive metadata with json
	void httpieWithAcceptHeaderJson() {
		MvcTestResult response = invokeHome("HTTPie/0.8.0", "application/json");
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(bodyOf(response));
	}

	@Test
	void httpieWithAcceptHeaderTextPlain() {
		MvcTestResult response = invokeHome("HTTPie/0.8.0", "text/plain");
		validateHttpIeHelpContent(response);
	}

	@Test
	void unknownCliWithTextPlain() {
		MvcTestResult response = invokeHome(null, "text/plain");
		validateGenericHelpContent(response);
	}

	@Test
	void springBootCliReceivesJsonByDefault() {
		MvcTestResult response = invokeHome("SpringBootCli/1.2.0", "*/*");
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(bodyOf(response));
	}

	@Test
	void springBootCliWithAcceptHeaderText() {
		MvcTestResult response = invokeHome("SpringBootCli/1.2.0", "text/plain");
		validateSpringBootHelpContent(response);
	}

	@Test
	void doNotForceSslByDefault() {
		MvcTestResult response = invokeHome("curl/1.2.4", "*/*");
		String body = bodyOf(response);
		assertThat(body).as("Must not force https").doesNotContain("https://");
	}

	private void validateCurlHelpContent(MvcTestResult response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		assertThat(bodyOf(response)).contains("Spring Initializr", "Examples:", "curl");
	}

	private void validateHttpIeHelpContent(MvcTestResult response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		assertThat(bodyOf(response)).contains("Spring Initializr", "Examples:", "http").doesNotContain("curl");
	}

	private void validateGenericHelpContent(MvcTestResult response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		assertThat(bodyOf(response)).contains("Spring Initializr").doesNotContain("Examples:", "curl");
	}

	private void validateSpringBootHelpContent(MvcTestResult response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response).containsHeader(HttpHeaders.ETAG);
		assertThat(bodyOf(response)).contains("Service capabilities", "Supported dependencies")
			.doesNotContain("Examples:", "curl", "Parameters");
	}

}
