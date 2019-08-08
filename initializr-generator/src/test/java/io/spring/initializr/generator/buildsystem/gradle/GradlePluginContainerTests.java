/*
 * Copyright 2012-2019 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradlePluginContainer}.
 *
 * @author HaITao Zhang
 */
public class GradlePluginContainerTests {

	@Test
	void addGradlePluginWithOnlyId() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void addGradlePluginWithConsumer() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example", (plugin) -> plugin.setVersion("1.0"));
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin).isInstanceOf(StandardGradlePlugin.class);
			assertThat(((StandardGradlePlugin) plugin).getVersion()).isEqualTo("1.0");
			assertThat(plugin.isApply()).isFalse();
		});

	}

	@Test
	void applyGradlePlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isTrue();
		});
	}

	@Test
	void applyGradlePluginShouldNotOverrideGradlePluginThatWasAlreadyAdded() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void addGradlePluginShouldNotOverrideGradlePluginThatWasAlreadyApplied() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		pluginContainer.add("com.example");
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isTrue();
		});
	}

}
