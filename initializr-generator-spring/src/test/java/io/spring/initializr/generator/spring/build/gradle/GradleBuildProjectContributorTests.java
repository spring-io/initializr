/*
 * Copyright 2012 - present the original author or authors.
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
	void groovyDslGradleBuildIsContributedInProjectStructure(@TempDir Path projectDir) throws IOException {
		GradleBuild build = new GradleBuild();
		groovyDslGradleBuildProjectContributor(build, IndentingWriterFactory.withDefaultSettings())
			.contribute(projectDir);
		Path buildGradle = projectDir.resolve("build.gradle");
		assertThat(buildGradle).isRegularFile();
	}

	@Test
	void groovyDslGradleBuildIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.settings().group("com.example").version("1.0.0-SNAPSHOT");
		build.buildscript((buildscript) -> buildscript.ext("someVersion", "'1.2.3'"));
		List<String> lines = generateBuild(
				groovyDslGradleBuildProjectContributor(build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("buildscript {", "    ext {", "        someVersion = '1.2.3'", "    }", "}");
		assertThat(lines).containsSequence("group = 'com.example'", "version = '1.0.0-SNAPSHOT'");
	}

	@Test
	void groovyDslGradleBuildIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(new SimpleIndentStrategy("    "),
				(factory) -> factory.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.buildscript((buildscript) -> buildscript.ext("someVersion", "'1.2.3'"));
		List<String> lines = generateBuild(groovyDslGradleBuildProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("buildscript {", "  ext {", "    someVersion = '1.2.3'", "  }", "}");
	}

	@Test
	void kotlinDslGradleBuildIsContributedInProjectStructure(@TempDir Path projectDir) throws IOException {
		GradleBuild build = new GradleBuild();
		kotlinDslGradleBuildProjectContributor(build, IndentingWriterFactory.withDefaultSettings())
			.contribute(projectDir);
		Path buildGradleKts = projectDir.resolve("build.gradle.kts");
		assertThat(buildGradleKts).isRegularFile();
	}

	@Test
	void kotlinDslGradleBuildIsContributedToProject() throws IOException {
		GradleBuild build = new GradleBuild();
		build.settings().group("com.example").version("1.0.0-SNAPSHOT");
		List<String> lines = generateBuild(
				kotlinDslGradleBuildProjectContributor(build, IndentingWriterFactory.withDefaultSettings()));
		assertThat(lines).containsSequence("group = \"com.example\"", "version = \"1.0.0-SNAPSHOT\"");
	}

	@Test
	void kotlinDslGradleBuildIsContributedUsingGradleContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(new SimpleIndentStrategy("    "),
				(factory) -> factory.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		GradleBuild build = new GradleBuild();
		build.plugins().add("java");
		List<String> lines = generateBuild(kotlinDslGradleBuildProjectContributor(build, indentingWriterFactory));
		assertThat(lines).containsSequence("plugins {", "  java", "}");
	}

	private List<String> generateBuild(GradleBuildProjectContributor contributor) throws IOException {
		StringWriter writer = new StringWriter();
		contributor.writeBuild(writer);
		return TextTestUtils.readAllLines(writer.toString());
	}

	private GradleBuildProjectContributor groovyDslGradleBuildProjectContributor(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory) {
		return new GradleBuildProjectContributor(new GroovyDslGradleBuildWriter(), build, indentingWriterFactory,
				"build.gradle");
	}

	private GradleBuildProjectContributor kotlinDslGradleBuildProjectContributor(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory) {
		return new GradleBuildProjectContributor(new KotlinDslGradleBuildWriter(), build, indentingWriterFactory,
				"build.gradle.kts");
	}

}
