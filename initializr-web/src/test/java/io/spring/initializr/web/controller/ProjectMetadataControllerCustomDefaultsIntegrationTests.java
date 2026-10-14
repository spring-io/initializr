/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.json.JSONException;
import org.json.JSONObject;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for {@link ProjectMetadataController} on a real http server.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectMetadataControllerCustomDefaultsIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	@Test
	void initializeRemoteConfig() throws Exception {
		InitializrMetadata localMetadata = this.metadataProvider.get();
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
			.withInitializrMetadata(new UrlResource(createUrl("/metadata/config")))
			.build();
		// Basic assertions
		assertThat(metadata.getDependencies().getContent()).hasSameSizeAs(localMetadata.getDependencies().getContent());
		assertThat(metadata.getTypes().getContent()).hasSameSizeAs(localMetadata.getTypes().getContent());
		assertThat(metadata.getBootVersions().getContent()).hasSameSizeAs(localMetadata.getBootVersions().getContent());
		assertThat(metadata.getPackagings().getContent()).hasSameSizeAs(localMetadata.getPackagings().getContent());
		assertThat(metadata.getJavaVersions().getContent()).hasSameSizeAs(localMetadata.getJavaVersions().getContent());
		assertThat(metadata.getLanguages().getContent()).hasSameSizeAs(localMetadata.getLanguages().getContent());
	}

	@Test
	void textPlainNotAccepted() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/metadata/config", String.class, null, "text/plain"))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE));
	}

	@Test
	void validateJson() throws JSONException {
		ResponseEntity<String> response = execute("/metadata/config", String.class, null, "application/json");
		validateContentType(response, MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject(response.getBody());
		JSONObject expected = readJsonFrom("metadata/config/test-default.json");
		JSONAssert.assertEquals(expected, json, JSONCompareMode.STRICT);
	}

	@Test
	void metadataClientEndpoint() {
		ResponseEntity<String> response = execute("/metadata/client", String.class, null, "application/json");
		validateDefaultMetadata(response);
	}

	@Test
	void dependenciesNoAcceptHeaderWithNoBootVersion() throws JSONException {
		validateDependenciesMetadata("*/*", DEFAULT_METADATA_MEDIA_TYPE);
	}

	@Test
	void dependenciesV21WithNoBootVersion() throws JSONException {
		validateDependenciesMetadata("application/vnd.initializr.v2.1+json", DEFAULT_METADATA_MEDIA_TYPE);
	}

	@Test
	void dependenciesV22WithNoBootVersion() throws JSONException {
		validateDependenciesMetadata("application/vnd.initializr.v2.2+json",
				InitializrMetadataVersion.V2_2.getMediaType());
	}

	@Test
	void dependenciesV23WithNoBootVersion() throws JSONException {
		validateDependenciesMetadata("application/vnd.initializr.v2.3+json", CURRENT_METADATA_MEDIA_TYPE);
	}

	private void validateDependenciesMetadata(String acceptHeader, MediaType expectedMediaType) throws JSONException {
		ResponseEntity<String> response = execute("/dependencies", String.class, null, acceptHeader);
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, expectedMediaType);
		validateDependenciesOutput("2.4.4", response.getBody());
	}

	@Test
	void filteredDependencies() throws JSONException {
		ResponseEntity<String> response = execute("/dependencies?bootVersion=2.6.1", String.class, null,
				"application/json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, DEFAULT_METADATA_MEDIA_TYPE);
		validateDependenciesOutput("2.6.1", response.getBody());
	}

	protected void validateDependenciesOutput(String version, @Nullable String actual) throws JSONException {
		JSONObject expected = readJsonFrom("metadata/dependencies/test-dependencies-" + version + ".json");
		JSONAssert.assertEquals(expected, new JSONObject(actual), JSONCompareMode.STRICT);
	}

}
