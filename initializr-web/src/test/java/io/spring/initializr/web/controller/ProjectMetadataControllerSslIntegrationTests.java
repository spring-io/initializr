/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ProjectMetadataController} with {@code forceSsl} enabled.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-ssl" })
class ProjectMetadataControllerSslIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void forceSslInMetadata() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.1+json");
		validateMetadata(response, InitializrMetadataVersion.V2_1.getMediaType(), "2.1.0-ssl", JSONCompareMode.STRICT);
	}

	@Test
	void forceSslInMetadataV2() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0-ssl", JSONCompareMode.STRICT);
	}

}
