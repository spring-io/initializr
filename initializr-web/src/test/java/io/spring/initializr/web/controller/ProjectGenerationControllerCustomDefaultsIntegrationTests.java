/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.buildsystem.maven.MavenBuildAssert;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ProjectGenerationController} that uses custom defaults.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-defaults" })
class ProjectGenerationControllerCustomDefaultsIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	// see defaults customization

	@Test
	void generateDefaultPom() {
		String content = bodyOf(this.mvc.get().uri("/pom.xml?dependencies=web").exchange());
		MavenBuildAssert pomAssert = new MavenBuildAssert(content);
		pomAssert.hasGroupId("org.foo")
			.hasArtifactId("foo-bar")
			.hasVersion("1.2.4-SNAPSHOT")
			.doesNotHaveNode("/project/packaging")
			.hasName("FooBar")
			.hasDescription("FooBar Project");
	}

}
