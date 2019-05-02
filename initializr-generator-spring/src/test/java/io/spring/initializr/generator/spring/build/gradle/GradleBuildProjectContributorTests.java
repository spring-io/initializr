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
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleBuildWriter;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleBuildWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.test.io.TextTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleBuildProjectContributor}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 */
class GradleBuildProjectContributorTests {

	@Test
	void groovyDslGradleBuildIsContributedInProjectStructure(@TempDir Path projectDir)
			throws IOException {
		GradleBuild build = new GradleBuild();
		groovyDslGradleBuildProjectContributor(build,
				IndentingWriterFactory.withDefaultSettings()).contribute(projectDir);
		Path buildGradle = projectDir.resolve("build.gradle");
		assertThat(buildGradle).isRegularFile();
	}

	@Test
	void groovyDslGradleBuildIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.setGroup("com.example");
		build.setVersion("1.0.0-SNAPSHOT");
		build.buildscript((buildscript) -> buildscript.ext("someVersion", "'1.2.3'"));
		List<String> lines = generateBuild(groovyDslGradleBuildProjectContributor(build,
				IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("buildscript {", "    ext {",
				"        someVersion = '1.2.3'", "    }", "}");
		assertThat(lines).containsSequence("group = 'com.example'",
				"version = '1.0.0-SNAPSHOT'");
	}

	@Test
	void groovyDslGradleBuildIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.buildscript((buildscript) -> buildscript.ext("someVersion", "'1.2.3'"));
		List<String> lines = generateBuild(
				groovyDslGradleBuildProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("buildscript {", "  ext {",
				"    someVersion = '1.2.3'", "  }", "}");
	}

	@Test
	void kotlinDslGradleBuildIsContributedInProjectStructure(@TempDir Path projectDir)
			throws IOException {
		GradleBuild build = new GradleBuild();
		kotlinDslGradleBuildProjectContributor(build,
				IndentingWriterFactory.withDefaultSettings()).contribute(projectDir);
		Path buildGradleKts = projectDir.resolve("build.gradle.kts");
		assertThat(buildGradleKts).isRegularFile();
	}

	@Test
	void kotlinDslGradleBuildIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.setGroup("com.example");
		build.setVersion("1.0.0-SNAPSHOT");
		List<String> lines = generateBuild(kotlinDslGradleBuildProjectContributor(build,
				IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("group = \"com.example\"",
				"version = \"1.0.0-SNAPSHOT\"");
	}

	@Test
	void kotlinDslGradleBuildIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.addPlugin("java");
		List<String> lines = generateBuild(
				kotlinDslGradleBuildProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("plugins {", "  java", "}");
	}

	private List<String> generateBuild(GradleBuildProjectContributor contributor)
			throws IOException {
		StringWriter writer = new StringWriter();
		contributor.writeBuild(writer);
		return TextTestUtils.readAllLines(writer.toString());
	}

	private GradleBuildProjectContributor groovyDslGradleBuildProjectContributor(
			GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
		return new GradleBuildProjectContributor(new GroovyDslGradleBuildWriter(), build,
				indentingWriterFactory, "build.gradle");
	}

	private GradleBuildProjectContributor kotlinDslGradleBuildProjectContributor(
			GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
		return new GradleBuildProjectContributor(new KotlinDslGradleBuildWriter(), build,
				indentingWriterFactory, "build.gradle.kts");
	}

}
