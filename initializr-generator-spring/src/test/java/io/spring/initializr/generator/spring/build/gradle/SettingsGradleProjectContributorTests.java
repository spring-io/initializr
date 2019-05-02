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
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleSettingsWriter;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleSettingsWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SettingsGradleProjectContributorTests}.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
class SettingsGradleProjectContributorTests {

	@TempDir
	Path directory;

	@Test
	void groovyDslGradleSettingsIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(groovyDslSettingsGradleProjectContributor(
				build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        mavenCentral()", "        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void groovyDslGradleSettingsIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(
				groovyDslSettingsGradleProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("pluginManagement {", "  repositories {",
				"    mavenCentral()", "    gradlePluginPortal()", "  }", "}");
	}

	@Test
	void groovyDslGradleSettingsDoesNotUseRepositories() throws IOException {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		List<String> lines = generateSettings(groovyDslSettingsGradleProjectContributor(
				build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void kotlinDslGradleSettingsIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(kotlinDslSettingsGradleProjectContributor(
				build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        mavenCentral()", "        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void kotlinDslGradleSettingsIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(
				kotlinDslSettingsGradleProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("pluginManagement {", "  repositories {",
				"    mavenCentral()", "    gradlePluginPortal()", "  }", "}");
	}

	@Test
	void kotlinDslGradleSettingsDoesNotUseRepositories() throws IOException {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		List<String> lines = generateSettings(kotlinDslSettingsGradleProjectContributor(
				build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        gradlePluginPortal()", "    }", "}");
	}

	private List<String> generateSettings(SettingsGradleProjectContributor contributor)
			throws IOException {
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		contributor.contribute(projectDir);
		return new ProjectStructure(projectDir).readAllLines("test.gradle");
	}

	private SettingsGradleProjectContributor groovyDslSettingsGradleProjectContributor(
			GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
		return new SettingsGradleProjectContributor(build, indentingWriterFactory,
				new GroovyDslGradleSettingsWriter(), "test.gradle");
	}

	private SettingsGradleProjectContributor kotlinDslSettingsGradleProjectContributor(
			GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
		return new SettingsGradleProjectContributor(build, indentingWriterFactory,
				new KotlinDslGradleSettingsWriter(), "test.gradle");
	}

}
