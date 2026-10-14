/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for a service that restricts the platform versions it supports to a
 * subset of the available versions.
 *
 * @author Moritz Halbritter
 */
@ActiveProfiles({ "test-default", "test-platform-range" })
class PlatformVersionRangeIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void generateProjectWithWildcardPlatformVersionOutOfRange() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/starter.tgz?bootVersion=2.3.x", byte[].class, null, (String[]) null))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getResponseBodyAsString()).contains("Invalid Spring Boot version", "2.3.10.RELEASE",
						"Spring Boot compatibility range is");
			});
	}

	@Test
	void getDependenciesWithWildcardPlatformVersionOutOfRange() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/dependencies?bootVersion=2.3.x", String.class, null,
					"application/vnd.initializr.v2.1+json"))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				// The resolved version is reported, not the wildcard that was requested
				assertThat(ex.getResponseBodyAsString())
					.contains("\"message\":\"Invalid Spring Boot version '2.3.10.RELEASE', "
							+ "Spring Boot compatibility range is >=2.4.0 and <2.5.0-SNAPSHOT\"");
			});
	}

}
