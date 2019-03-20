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
 * Tests for {@link MavenBuild}.
 *
 * @author Stephane Nicoll
 */
class MavenBuildTests {

	@Test
	void mavenPluginCanBeConfigured() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin").execution("first",
				(first) -> first.goal("run-this"));
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
		assertThat(testPlugin.getVersion()).isNull();
		assertThat(testPlugin.getExecutions()).hasSize(1);
		assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
		assertThat(testPlugin.getExecutions().get(0).getGoals())
				.containsExactly("run-this");
	}

	@Test
	void mavenPluginVersionCanBeAmended() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin");
		build.plugin("com.example", "test-plugin", "1.0.0");
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
		assertThat(testPlugin.getVersion()).isEqualTo("1.0.0");
	}

	@Test
	void mavenPluginVersionCanBeAmendedWithCustomizer() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin", "1.0.0");
		build.plugin("com.example", "test-plugin").setVersion(null);
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
		assertThat(testPlugin.getVersion()).isNull();
	}

	@Test
	void mavenPluginVersionIsNotLostOnAmend() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin", "1.0.0");
		build.plugin("com.example", "test-plugin");
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
		assertThat(testPlugin.getVersion()).isEqualTo("1.0.0");
	}

	@Test
	void mavenPluginExecutionCanBeAmended() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin").execution("first",
				(first) -> first.goal("run-this"));
		build.plugin("com.example", "test-plugin").execution("first",
				(first) -> first.goal("run-that"));
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getExecutions()).hasSize(1);
		assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
		assertThat(testPlugin.getExecutions().get(0).getGoals())
				.containsExactly("run-this", "run-that");
	}

}
