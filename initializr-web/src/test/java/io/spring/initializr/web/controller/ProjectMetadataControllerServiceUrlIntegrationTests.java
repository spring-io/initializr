/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
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
		return bodyOf(this.mvc.get()
			.uri("/")
			.header(HttpHeaders.HOST, host)
			.accept(InitializrMetadataVersion.V2_1.getMediaType())
			.exchange());
	}

}
