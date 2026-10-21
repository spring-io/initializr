/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectGenerationController} error responses, as rendered
 * by a running server.
 *
 * @author Moritz Halbritter
 */
@ActiveProfiles("test-default")
class ProjectGenerationControllerErrorIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Test
	void invalidRequestRendersJsonErrorWithMessage() {
		assertThat(downloadArchive("/starter.zip?dependencies=foo")).hasStatus(HttpStatus.BAD_REQUEST)
			.bodyJson()
			.extractingPath("$.message")
			.isEqualTo("Unknown dependency 'foo' check project metadata");
	}

}
