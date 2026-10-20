/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommandLineMetadataController} with {@code serviceUrl}
 * set.
 *
 * @author Moritz Halbritter
 */
@ActiveProfiles("test-default")
@TestPropertySource(properties = { "initializr.env.service-url=http://start.example.com/initializr",
		"initializr.env.force-ssl=true" })
class CommandLineMetadataControllerServiceUrlIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	private static final String SPOOFED_HOST = "evil.example.com";

	@Test
	void serviceUrlTakesPrecedenceOverRequestAndForceSsl() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.HOST, SPOOFED_HOST);
		headers.set(HttpHeaders.USER_AGENT, "curl/1.2.4");
		ResponseEntity<String> response = getRestTemplate().exchange("/", HttpMethod.GET, new HttpEntity<>(headers),
				String.class);
		assertThat(response.getBody()).contains("http://start.example.com/initializr/starter.zip")
			.doesNotContain(SPOOFED_HOST)
			.doesNotContain("https://start.example.com");
	}

}
