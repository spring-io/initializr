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

import java.util.Arrays;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MavenProfileTests {

	@Mock
	private BuildItemResolver buildItemResolver;

	@Test
	void profileEmpty() {
		MavenProfile profile = new MavenProfile.Builder("profile1", this.buildItemResolver).build();
		assertThat(profile.getId()).isEqualTo("profile1");
		assertThat(profile.getActivation()).isNull();
		assertThat(profile.getBuild()).isNull();
		assertThat(profile.getModules()).isNull();
		assertThat(profile.getRepositories().isEmpty()).isTrue();
		assertThat(profile.getPluginRepositories().isEmpty()).isTrue();
		assertThat(profile.getDependencies().isEmpty()).isTrue();
		assertThat(profile.getReporting()).isNull();
		assertThat(profile.getDependencyManagement().isEmpty()).isTrue();
		assertThat(profile.getDistributionManagement().isEmpty()).isTrue();
		assertThat(profile.getProperties()).isNull();
	}

	@Test
	void profileWithFullData() {
		MavenProfile profile = new MavenProfile.Builder("profile1", this.buildItemResolver)
				.activation((activation) -> activation.activeByDefault(true))
				.build((build) -> build.defaultGoal("goal1")).module("module1").module("module2")
				.repositories((repositories) -> repositories.add("repository1",
						MavenRepository.withIdAndUrl("repository1", "url").build()))
				.pluginRepositories((pluginRepositories) -> pluginRepositories.add("pluginRepository1",
						MavenRepository.withIdAndUrl("pluginRepository1", "url2").build()))
				.dependencies((dependencies) -> dependencies.add("dependency1",
						Dependency.withCoordinates("com.example", "demo").build()))
				.reporting((reporting) -> reporting.outputDirectory("directory1"))
				.dependencyManagement((dependencyManagement) -> dependencyManagement.add("dependencyManagement1",
						BillOfMaterials.withCoordinates("com.example1", "demo1").build()))
				.distributionManagement((distributionManagement) -> distributionManagement.downloadUrl("url"))
				.properties((properties) -> properties.add("name1", "value1")).build();

		assertThat(profile.getId()).isEqualTo("profile1");
		assertThat(profile.getActivation()).isNotNull();
		assertThat(profile.getActivation().getActiveByDefault()).isTrue();
		assertThat(profile.getBuild()).isNotNull();
		assertThat(profile.getBuild().getDefaultGoal()).isEqualTo("goal1");
		assertThat(profile.getModules()).isNotNull();
		assertThat(profile.getModules()).isEqualTo(Arrays.asList("module1", "module2"));
		assertThat(profile.getRepositories()).isNotNull();
		assertThat(profile.getRepositories().has("repository1")).isTrue();
		assertThat(profile.getPluginRepositories()).isNotNull();
		assertThat(profile.getPluginRepositories().has("pluginRepository1")).isTrue();
		assertThat(profile.getDependencies()).isNotNull();
		assertThat(profile.getDependencies().has("dependency1")).isTrue();
		assertThat(profile.getReporting()).isNotNull();
		assertThat(profile.getReporting().getOutputDirectory()).isEqualTo("directory1");
		assertThat(profile.getDependencyManagement()).isNotNull();
		assertThat(profile.getDependencyManagement().has("dependencyManagement1")).isTrue();
		assertThat(profile.getProperties()).isNotNull();
		assertThat(profile.getProperties().getSettings()).hasOnlyOneElementSatisfying((settings) -> {
			assertThat(settings.getName()).isEqualTo("name1");
			assertThat(settings.getValue()).isEqualTo("value1");
		});
	}

}
