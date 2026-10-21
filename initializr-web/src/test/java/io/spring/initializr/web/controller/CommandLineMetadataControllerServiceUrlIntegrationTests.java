/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

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
		MvcTestResult response = this.mvc.get()
			.uri("/")
			.header(HttpHeaders.HOST, SPOOFED_HOST)
			.header(HttpHeaders.USER_AGENT, "curl/1.2.4")
			.exchange();
		assertThat(bodyOf(response)).contains("http://start.example.com/initializr/starter.zip")
			.doesNotContain(SPOOFED_HOST)
			.doesNotContain("https://start.example.com");
	}

}
