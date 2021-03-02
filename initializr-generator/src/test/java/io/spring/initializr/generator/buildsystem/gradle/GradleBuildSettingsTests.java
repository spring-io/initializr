/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		assertThatIllegalArgumentException()
				.isThrownBy(() -> settingsBuilder.mapPlugin("test",
						Dependency.withCoordinates("com.example", "plugin")
								.version(VersionReference.ofProperty("test.version")).build()))
				.withMessage("Mapping for plugin 'test' must have a version");
	}

	@Test
	void settingsFromBuilderClonePluginMappings() {
		Builder settingsBuilder = new Builder();
		settingsBuilder.mapPlugin("test",
				Dependency.withCoordinates("com.example", "plugin").version(VersionReference.ofValue("1.0.0")).build());
		GradleBuildSettings firstSettings = settingsBuilder.build();
		settingsBuilder.mapPlugin("another", Dependency.withCoordinates("com.example", "another")
				.version(VersionReference.ofValue("2.0.0")).build());
		assertThat(firstSettings.getPluginMappings()).singleElement()
				.satisfies((pluginMapping) -> assertThat(pluginMapping.getId()).isEqualTo("test"));
	}

}
