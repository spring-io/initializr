/*
 * Copyright 2012-2020 the original author or authors.
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
 * @author Stephane Nicoll
 */
class GradlePluginContainerTests {

	@Test
	void addPluginWithId() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void addPluginWithConsumer() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example", (plugin) -> plugin.setVersion("1.0"));
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin).isInstanceOf(StandardGradlePlugin.class);
			assertThat(((StandardGradlePlugin) plugin).getVersion()).isEqualTo("1.0");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void addPluginSeveralTimeReuseConfiguration() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example", (plugin) -> {
			assertThat(plugin.getVersion()).isNull();
			plugin.setVersion("1.0");
		});
		pluginContainer.add("com.example", (plugin) -> {
			assertThat(plugin.getVersion()).isEqualTo("1.0");
			plugin.setVersion("2.0");
		});
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin).isInstanceOf(StandardGradlePlugin.class);
			assertThat(((StandardGradlePlugin) plugin).getVersion()).isEqualTo("2.0");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void applyGradlePlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isTrue();
		});
	}

	@Test
	void applyGradlePluginSeveralTimesApplyOnlyOnce() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isTrue();
		});
	}

	@Test
	void applyGradlePluginShouldNotOverrideGradlePluginThatWasAlreadyAdded() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isFalse();
		});
	}

	@Test
	void addGradlePluginShouldNotOverrideGradlePluginThatWasAlreadyApplied() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		pluginContainer.add("com.example");
		assertThat(pluginContainer.values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("com.example");
			assertThat(plugin.isApply()).isTrue();
		});
	}

	@Test
	void isEmptyWithEmptyContainer() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		assertThat(pluginContainer.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithRegisteredPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		assertThat(pluginContainer.isEmpty()).isFalse();
	}

	@Test
	void hasPluginWithMatchingStandardPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		assertThat(pluginContainer.has("com.example")).isTrue();
	}

	@Test
	void hasPluginWithMatchingAppliedPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.has("com.example")).isTrue();
	}

	@Test
	void hasPluginWithNonMatchingPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.has("com.example.another")).isFalse();
	}

	@Test
	void removeWithMatchingStandardPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.add("com.example");
		assertThat(pluginContainer.remove("com.example")).isTrue();
		assertThat(pluginContainer.isEmpty()).isTrue();
	}

	@Test
	void removeWithMatchingAppliedPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.remove("com.example")).isTrue();
		assertThat(pluginContainer.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingPlugin() {
		GradlePluginContainer pluginContainer = new GradlePluginContainer();
		pluginContainer.apply("com.example");
		assertThat(pluginContainer.remove("com.example.another")).isFalse();
		assertThat(pluginContainer.isEmpty()).isFalse();
	}

}
