/*
 * Copyright 2012-2020 the original author or authors.
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

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommandLineMetadataController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class CommandLineMetadataControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void curlReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		validateCurlHelpContent(response);
	}

	@Test
	// make sure curl can still receive metadata with json
	void curlWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "application/json");
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(response.getBody());
	}

	@Test
	void curlWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "text/plain");
		validateCurlHelpContent(response);
	}

	@Test
	void httpieReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "*/*");
		validateHttpIeHelpContent(response);
	}

	@Test
	// make sure curl can still receive metadata with json
	void httpieWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome("HTTPie/0.8.0", "application/json");
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(response.getBody());
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
		validateContentType(response, AbstractInitializrIntegrationTests.DEFAULT_METADATA_MEDIA_TYPE);
		validateDefaultMetadata(response.getBody());
	}

	@Test
	void springBootCliWithAcceptHeaderText() {
		ResponseEntity<String> response = invokeHome("SpringBootCli/1.2.0", "text/plain");
		validateSpringBootHelpContent(response);
	}

	@Test
	void doNotForceSslByDefault() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		String body = response.getBody();
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
		assertThat(response.getBody()).contains("Spring Initializr", "Examples:", "http").doesNotContain("curl");
	}

	private void validateGenericHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody()).contains("Spring Initializr").doesNotContain("Examples:", "curl");
	}

	private void validateSpringBootHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		assertThat(response.getBody()).contains("Service capabilities", "Supported dependencies")
				.doesNotContain("Examples:", "curl");
	}

}
