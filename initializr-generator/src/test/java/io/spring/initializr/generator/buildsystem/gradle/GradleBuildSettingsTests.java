/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSettings.Builder;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link GradleBuildSettings}.
 *
 * @author Stephane Nicoll
 */
class GradleBuildSettingsTests {

	@Test
	void mapPluginWithoutVersionIsNotAllowed() {
		Builder settingsBuilder = new Builder();
		assertThatIllegalArgumentException().isThrownBy(
				() -> settingsBuilder.mapPlugin("test", Dependency.withCoordinates("com.example", "plugin").build()))
			.withMessage("Mapping for plugin 'test' must have a version");
	}

	@Test
	void mapPluginWithVersionReferenceIsNotAllowed() {
		Builder settingsBuilder = new Builder();
		assertThatIllegalArgumentException().isThrownBy(() -> settingsBuilder.mapPlugin("test",
				Dependency.withCoordinates("com.example", "plugin")
					.version(VersionReference.ofProperty("test.version"))
					.build()))
			.withMessage("Mapping for plugin 'test' must have a version");
	}

	@Test
	void settingsFromBuilderClonePluginMappings() {
		Builder settingsBuilder = new Builder();
		settingsBuilder.mapPlugin("test",
				Dependency.withCoordinates("com.example", "plugin").version(VersionReference.ofValue("1.0.0")).build());
		GradleBuildSettings firstSettings = settingsBuilder.build();
		settingsBuilder.mapPlugin("another",
				Dependency.withCoordinates("com.example", "another")
					.version(VersionReference.ofValue("2.0.0"))
					.build());
		assertThat(firstSettings.getPluginMappings()).singleElement()
			.satisfies((pluginMapping) -> assertThat(pluginMapping.getId()).isEqualTo("test"));
	}

}
