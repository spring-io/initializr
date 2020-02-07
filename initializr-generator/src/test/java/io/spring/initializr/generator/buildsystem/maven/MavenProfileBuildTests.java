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

package io.spring.initializr.generator.buildsystem.maven;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MavenProfileBuildTests {

	@Test
	void profileBuildEmpty() {
		MavenProfileBuild profileBuild = new MavenProfileBuild.Builder().build();
		assertThat(profileBuild.getDefaultGoal()).isNull();
		assertThat(profileBuild.getDirectory()).isNull();
		assertThat(profileBuild.getFinalName()).isNull();
		assertThat(profileBuild.getFilters()).isNull();
		assertThat(profileBuild.getResources().isEmpty()).isTrue();
		assertThat(profileBuild.getTestResources().isEmpty()).isTrue();
		assertThat(profileBuild.getPluginManagement()).isNull();
		assertThat(profileBuild.getPlugins().isEmpty()).isTrue();
	}

	@Test
	void profileBuildWithFullData() {
		MavenProfileBuild profileBuild = new MavenProfileBuild.Builder().defaultGoal("goal1").directory("directory1")
				.finalName("file1").filter("filter1").filter("filter2")
				.resources((resources) -> resources.add("resource1"))
				.testResources((testResources) -> testResources.add("testResources1"))
				.pluginManagement(
						(pluginManagement) -> pluginManagement.plugins((plugins) -> plugins.add("com.example", "demo")))
				.plugins((plugins) -> plugins.add("com.example1", "demo1")).build();

		assertThat(profileBuild.getDefaultGoal()).isEqualTo("goal1");
		assertThat(profileBuild.getDirectory()).isEqualTo("directory1");
		assertThat(profileBuild.getFinalName()).isEqualTo("file1");
		assertThat(profileBuild.getFilters()).isEqualTo(Arrays.asList("filter1", "filter2"));
		assertThat(profileBuild.getResources()).isNotNull();
		assertThat(profileBuild.getResources().has("resource1")).isTrue();
		assertThat(profileBuild.getTestResources()).isNotNull();
		assertThat(profileBuild.getTestResources().has("testResources1")).isTrue();
		assertThat(profileBuild.getPluginManagement()).isNotNull();
		assertThat(profileBuild.getPluginManagement().getPlugins()).isNotNull();
		assertThat(profileBuild.getPluginManagement().getPlugins().has("com.example", "demo")).isTrue();
		assertThat(profileBuild.getPlugins()).isNotNull();
		assertThat(profileBuild.getPlugins().has("com.example1", "demo1")).isTrue();
	}

}
