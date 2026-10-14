/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommandLineMetadataController} with {@code forceSsl}
 * enabled.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-ssl" })
class CommandLineMetadataControllerSslIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void forceSsl() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		String body = response.getBody();
		assertThat(body).as("Must force https").contains("https://start.example.com/");
		assertThat(body).as("Must force https").doesNotContain("http://");
	}

}
