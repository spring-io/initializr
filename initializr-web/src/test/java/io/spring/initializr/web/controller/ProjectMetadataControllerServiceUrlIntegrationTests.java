/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.util.List;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectMetadataController} with {@code serviceUrl} set.
 *
 * @author Moritz Halbritter
 */
@ActiveProfiles("test-default")
@TestPropertySource(properties = "initializr.env.service-url=https://start.spring.io")
class ProjectMetadataControllerServiceUrlIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	private static final String SPOOFED_HOST = "evil.example.com";

	@Test
	void linksUseServiceUrl() {
		String body = invokeHomeWithHost(SPOOFED_HOST);
		assertThat(body).contains("\"href\":\"https://start.spring.io/starter.zip?type=maven-project");
		assertThat(body).contains("\"href\":\"https://start.spring.io/dependencies{?bootVersion}\"");
		assertThat(body).doesNotContain(SPOOFED_HOST);
	}

	private String invokeHomeWithHost(String host) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.HOST, host);
		headers.setAccept(List.of(InitializrMetadataVersion.V2_1.getMediaType()));
		ResponseEntity<String> response = getRestTemplate().exchange("/", HttpMethod.GET, new HttpEntity<>(headers),
				String.class);
		assertThat(response.getBody()).isNotNull();
		return response.getBody();
	}

}
