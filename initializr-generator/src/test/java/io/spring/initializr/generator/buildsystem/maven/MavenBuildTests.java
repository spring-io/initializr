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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild.Resource;

/**
 * Tests for {@link MavenBuild}.
 *
 * @author Stephane Nicoll
 * @author Olga Maciaszek-Sharma
 */
class MavenBuildTests {

	@Test
	void mavenPluginCanBeConfigured() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin").execution("first", (first) -> first.goal("run-this"));
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(testPlugin.getArtifactId()).isEqualTo("test-plugin");
		assertThat(testPlugin.getVersion()).isNull();
		assertThat(testPlugin.getExecutions()).hasSize(1);
		assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
		assertThat(testPlugin.getExecutions().get(0).getGoals()).containsExactly("run-this");
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
		build.plugin("com.example", "test-plugin").execution("first", (first) -> first.goal("run-this"));
		build.plugin("com.example", "test-plugin").execution("first", (first) -> first.goal("run-that"));
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin testPlugin = build.getPlugins().get(0);
		assertThat(testPlugin.getExecutions()).hasSize(1);
		assertThat(testPlugin.getExecutions().get(0).getId()).isEqualTo("first");
		assertThat(testPlugin.getExecutions().get(0).getGoals()).containsExactly("run-this", "run-that");
	}

	@Test
	void mavenPluginExtensionsNotLoadedByDefault() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin");

		MavenPlugin testPlugin = build.getPlugins().get(0);

		assertThat(testPlugin.isExtensions()).isFalse();
	}

	@Test
	void mavenPluginExtensionsCanBeLoaded() {
		MavenBuild build = new MavenBuild();
		build.plugin("com.example", "test-plugin").extensions();

		MavenPlugin testPlugin = build.getPlugins().get(0);

		assertThat(testPlugin.isExtensions()).isTrue();
	}

	@Test
	void mavenResourcesCanBeLoaded() {

		MavenBuild build = new MavenBuild();

		List<String> includes = Arrays.asList("**/*.yml");
		List<String> excludes = Arrays.asList("**/*.properties");

		build.resource("src/main/resources", (resource) -> {
			resource.include(includes);
			resource.targetPath("targetPath");
			resource.filtering(false);
			resource.excludes(excludes);
		});

		Resource resource = build.getResources().get(0);

		assertThat(resource.getIncludes().get(0)).isEqualTo("**/*.yml");
		assertThat(resource.getExcludes().get(0)).isEqualTo("**/*.properties");
		assertThat(resource.isFiltering()).isFalse();
		assertThat(resource.getTargetPath()).isEqualTo("targetPath");
		assertThat(resource.getDirectory()).isEqualTo("src/main/resources");

	}

}
