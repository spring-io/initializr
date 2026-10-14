/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.buildsystem.maven.MavenBuildAssert;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

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
		String content = getRestTemplate().getForObject(createUrl("/pom.xml?dependencies=web"), String.class);
		assertThat(content).isNotNull();
		MavenBuildAssert pomAssert = new MavenBuildAssert(content);
		pomAssert.hasGroupId("org.foo")
			.hasArtifactId("foo-bar")
			.hasVersion("1.2.4-SNAPSHOT")
			.doesNotHaveNode("/project/packaging")
			.hasName("FooBar")
			.hasDescription("FooBar Project");
	}

}
