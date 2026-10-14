/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ProjectGenerationController} on a real http server.
 *
 * @author HaiTao Zhang
 */
@ActiveProfiles("test-default")
class ProjectGenerationControllerArchiveIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Test
	void baseDirectorySeparatedBySpace() {
		ProjectStructure project = downloadZip("/starter.zip?artifactId=demo&baseDir=demo trial");
		assertDefaultProject(project.resolveModule("demo trial"));
	}

}
