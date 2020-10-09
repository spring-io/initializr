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
 * @author Olga Maciaszek-Sharma
 */
class MavenBuildTests {

	@Test
	void mavenResourcesEmptyByDefault() {
		MavenBuild build = new MavenBuild();
		assertThat(build.resources().isEmpty()).isTrue();
		assertThat(build.testResources().isEmpty()).isTrue();
	}

	@Test
	void mavenResourcesCanBeConfigured() {
		MavenBuild build = new MavenBuild();
		build.resources().add("src/main/custom", (resource) -> resource.filtering(true));
		assertThat(build.resources().values()).hasOnlyOneElementSatisfying((resource) -> {
			assertThat(resource.getDirectory()).isEqualTo("src/main/custom");
			assertThat(resource.isFiltering()).isTrue();
		});
		assertThat(build.testResources().isEmpty()).isTrue();
	}

	@Test
	void mavenTestResourcesCanBeConfigured() {
		MavenBuild build = new MavenBuild();
		build.testResources().add("src/test/custom", (resource) -> resource.excludes("**/*.gen"));
		assertThat(build.resources().isEmpty()).isTrue();
		assertThat(build.testResources().values()).hasOnlyOneElementSatisfying((resource) -> {
			assertThat(resource.getDirectory()).isEqualTo("src/test/custom");
			assertThat(resource.getExcludes()).containsExactly("**/*.gen");
		});
	}

	@Test
	void mavenPluginCanBeConfigured() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin",
				(plugin) -> plugin.execution("first", (first) -> first.goal("run-this")));
		assertThat(build.plugins().values()).hasOnlyOneElementSatisfying((testPlugin) -> {
			assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(testPlugin.getVersion()).isNull();
			assertThat(testPlugin.getExecutions()).hasSize(1);
			assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
			assertThat(testPlugin.getExecutions().get(0).getGoals()).containsExactly("run-this");
		});
	}

	@Test
	void mavenPluginVersionCanBeAmended() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin");
		build.plugins().add("com.example", "test-plugin", (plugin) -> plugin.version("1.0.0"));
		assertThat(build.plugins().values()).hasOnlyOneElementSatisfying((testPlugin) -> {
			assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(testPlugin.getVersion()).isEqualTo("1.0.0");
		});

	}

	@Test
	void mavenPluginVersionCanBeAmendedWithCustomizer() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin", (plugin) -> plugin.version("1.0.0"));
		build.plugins().add("com.example", "test-plugin", (plugin) -> plugin.version(null));
		assertThat(build.plugins().values()).hasOnlyOneElementSatisfying((testPlugin) -> {
			assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(testPlugin.getVersion()).isNull();
		});
	}

	@Test
	void mavenPluginVersionIsNotLostOnAmend() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin", (plugin) -> plugin.version("1.0.0"));
		build.plugins().add("com.example", "test-plugin");
		assertThat(build.plugins().values()).hasOnlyOneElementSatisfying((testPlugin) -> {
			assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
			assertThat(testPlugin.getVersion()).isEqualTo("1.0.0");
		});
	}

	@Test
	void mavenPluginExecutionCanBeAmended() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin",
				(plugin) -> plugin.execution("first", (first) -> first.goal("run-this")));
		build.plugins().add("com.example", "test-plugin",
				(plugin) -> plugin.execution("first", (first) -> first.goal("run-that")));
		assertThat(build.plugins().values()).hasOnlyOneElementSatisfying((testPlugin) -> {
			assertThat(testPlugin.getExecutions()).hasSize(1);
			assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
			assertThat(testPlugin.getExecutions().get(0).getGoals()).containsExactly("run-this", "run-that");
		});
	}

	@Test
	void mavenPluginExtensionsNotLoadedByDefault() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin");
		assertThat(build.plugins().values())
				.hasOnlyOneElementSatisfying((testPlugin) -> assertThat(testPlugin.isExtensions()).isFalse());
	}

	@Test
	void mavenPluginExtensionsCanBeLoaded() {
		MavenBuild build = new MavenBuild();
		build.plugins().add("com.example", "test-plugin", (plugin) -> plugin.extensions(true));
		assertThat(build.plugins().values())
				.hasOnlyOneElementSatisfying((testPlugin) -> assertThat(testPlugin.isExtensions()).isTrue());
	}

}
