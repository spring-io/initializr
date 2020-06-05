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
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectMetadataController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class ProjectMetadataControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

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
	void metadataWithInvalidPlatformVersion() {
		try {
			execute("/dependencies?bootVersion=1.5.17.RELEASE", String.class, "application/vnd.initializr.v2.1+json",
					"application/json");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getResponseBodyAsString().contains("1.5.17.RELEASE"));
		}
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
		getRequests().setFields("_links.maven-project", "dependencies.values[0]", "type.values[0]",
				"javaVersion.values[0]", "packaging.values[0]", "bootVersion.values[0]", "language.values[0]");
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.2+json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateMetadata(response.getBody(), "2.2.0");
	}

	@Test
	void metadataWithSeveralVersionsAndQualifier() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json;q=0.9",
				"application/vnd.initializr.v2.2+json");
		validateContentType(response, AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
		validateCurrentMetadata(response);
	}

	@Test
	void metadataWithSeveralVersionAndPreferenceOnInvalidVersion() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v5.4+json",
				"application/vnd.initializr.v2.2+json;q=0.9");
		validateContentType(response, AbstractInitializrIntegrationTests.CURRENT_METADATA_MEDIA_TYPE);
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
		try {
			invokeHome(null, "application/vnd.initializr.v5.4+json");
		}
		catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
		}
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

	private String getMetadataJson() {
		return getMetadataJson(null);
	}

	private String getMetadataJson(String userAgentHeader, String... acceptHeaders) {
		return invokeHome(userAgentHeader, acceptHeaders).getBody();
	}

}
