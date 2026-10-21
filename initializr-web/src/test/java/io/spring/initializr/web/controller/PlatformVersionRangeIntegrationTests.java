/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

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
		assertBadRequest(downloadArchive("/starter.tgz?bootVersion=2.3.x"), "Invalid Spring Boot version",
				"2.3.10.RELEASE", "Spring Boot compatibility range is");
	}

	@Test
	void getDependenciesWithWildcardPlatformVersionOutOfRange() {
		// The resolved version is reported, not the wildcard that was requested
		assertBadRequest(execute("/dependencies?bootVersion=2.3.x", null, "application/vnd.initializr.v2.1+json"),
				"Invalid Spring Boot version '2.3.10.RELEASE', "
						+ "Spring Boot compatibility range is >=2.4.0 and <2.5.0-SNAPSHOT");
	}

}
