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

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenPluginContainer}.
 *
 * @author HaiTao Zhang
 */
public class MavenPluginContainerTests {

	@Test
	void addMavenPlugin() {
		MavenPluginContainer pluginContainer = new MavenPluginContainer();
		pluginContainer.add("com.example", "test-plugin");
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getGroupId()).isEqualTo("com.example");
			assertThat(plugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(plugin.getVersion()).isNull();
		});
	}

	@Test
	void addMavenPluginWithConsumer() {
		MavenPluginContainer pluginContainer = new MavenPluginContainer();
		pluginContainer.add("com.example", "test-plugin", (plugin) -> {
			plugin.setVersion("1.0");
			plugin.execution("first", (first) -> first.goal("run-this"));
		});
		assertThat(pluginContainer.values()).hasSize(1);
		pluginContainer.values().findFirst().ifPresent((plugin) -> {
			assertThat(plugin.getGroupId()).isEqualTo("com.example");
			assertThat(plugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(plugin.getVersion()).isEqualTo("1.0");
			assertThat(plugin.getExecutions()).hasSize(1);
			assertThat(plugin.getExecutions().get(0).getId()).isEqualTo("first");
			assertThat(plugin.getExecutions().get(0).getGoals()).containsExactly("run-this");
		});
	}

}
