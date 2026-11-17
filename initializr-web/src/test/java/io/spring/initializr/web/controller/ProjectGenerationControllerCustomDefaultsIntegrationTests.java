/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
