/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleBuildWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinDslGradleBuildProjectContributor}.
 *
 * @author Jean-Baptiste Nizet
 */
class KotlinDslGradleBuildProjectContributorTests {

	@Test
	void gradleBuildIsContributedInProjectStructure(@TempDir Path projectDir)
			throws IOException {
		GradleBuild build = new GradleBuild();
		new KotlinDslGradleBuildProjectContributor(new KotlinDslGradleBuildWriter(),
				build, IndentingWriterFactory.withDefaultSettings())
						.contribute(projectDir);
		Path buildGradleKts = projectDir.resolve("build.gradle.kts");
		assertThat(buildGradleKts).isRegularFile();
	}

	@Test
	void gradleBuildIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.setGroup("com.example");
		build.setVersion("1.0.0-SNAPSHOT");
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("group = \"com.example\"",
				"version = \"1.0.0-SNAPSHOT\"");
	}

	@Test
	void gradleBuildIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.addPlugin("java");
		List<String> lines = generateBuild(build, indentingWriterFactory);
		assertThat(lines).containsSequence("plugins {", "  java", "}");
	}

	private List<String> generateBuild(GradleBuild build) throws IOException {
		return generateBuild(build, IndentingWriterFactory.withDefaultSettings());
	}

	private List<String> generateBuild(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory) throws IOException {
		StringWriter writer = new StringWriter();
		new KotlinDslGradleBuildProjectContributor(new KotlinDslGradleBuildWriter(),
				build, indentingWriterFactory).writeBuild(writer);
		return Arrays.asList(writer.toString().split("\\r?\\n"));
	}

}
