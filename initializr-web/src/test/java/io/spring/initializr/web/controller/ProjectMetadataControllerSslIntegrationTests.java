/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Integration tests for {@link ProjectMetadataController} with {@code forceSsl} enabled.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-ssl" })
class ProjectMetadataControllerSslIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void forceSslInMetadata() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2.1+json");
		validateMetadata(response, InitializrMetadataVersion.V2_1.getMediaType(), "2.1.0-ssl", JSONCompareMode.STRICT);
	}

	@Test
	void forceSslInMetadataV2() {
		MvcTestResult response = invokeHome(null, "application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0-ssl", JSONCompareMode.STRICT);
	}

}
