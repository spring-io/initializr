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

package io.spring.initializr.generator.spring.build.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SettingsGradleProjectContributor}.
 *
 * @author Andy Wilkinson
 */
class SettingsGradleProjectContributorTests {

	@TempDir
	Path directory;

	@Test
	void gradleSettingsIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        mavenCentral()", "        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void gradleSettingsIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(build, indentingWriterFactory);
		assertThat(lines).containsSequence("pluginManagement {", "  repositories {",
				"    mavenCentral()", "    gradlePluginPortal()", "  }", "}");
	}

	@Test
	void gradleSettingsDoesNotUseRepositories() throws IOException {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        gradlePluginPortal()", "    }", "}");
	}

	private List<String> generateSettings(GradleBuild build) throws IOException {
		return generateSettings(build, IndentingWriterFactory.withDefaultSettings());
	}

	private List<String> generateSettings(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory) throws IOException {
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		new SettingsGradleProjectContributor(build, indentingWriterFactory)
				.contribute(projectDir);
		return new ProjectStructure(projectDir).readAllLines("settings.gradle");
	}

}
