/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.SourceSet;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationPropertiesContributor}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class ApplicationPropertiesContributorTests {

	@TempDir
	@SuppressWarnings("NullAway.Init")
	Path directory;

	@Test
	void shouldWriteEmptyMainFileByDefault() throws IOException {
		new ApplicationPropertiesContributor(new ApplicationProperties(), TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		ProjectStructure project = new ProjectStructure(this.directory);
		assertThat(project).filePaths().containsOnly("src/main/resources/application.properties");
		assertThat(project).textFile("src/main/resources/application.properties").isEmpty();
	}

	@Test
	void shouldWriteAllValueTypes() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("string", "value");
		properties.add("long", 1L);
		properties.add("double", 0.1);
		properties.add("boolean", false);
		properties.add("collection", List.of("value1", "value2"));
		properties.add("empty-collection", Collections.emptyList());
		new ApplicationPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.lines()
			.containsExactly("string=value", "long=1", "double=0.1", "boolean=false", "collection=value1,value2",
					"empty-collection=");
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
			MAIN, , src/main/resources/application.properties
			MAIN, dev, src/main/resources/application-dev.properties
			TEST, , src/test/resources/application.properties
			TEST, integration, src/test/resources/application-integration.properties
			""")
	void shouldWriteProfileToMatchingFile(SourceSet sourceSet, @Nullable String profile, String expectedFile)
			throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		file(properties, sourceSet, profile).add("spring.application.name", "test");
		new ApplicationPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile(expectedFile)
			.lines()
			.containsExactly("spring.application.name=test");
	}

	@Test
	void shouldKeepProfilesWithSameKeyIsolated() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "main");
		properties.profile("dev").add("spring.application.name", "dev");
		new ApplicationPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		ProjectStructure project = new ProjectStructure(this.directory);
		assertThat(project).textFile("src/main/resources/application.properties")
			.lines()
			.containsExactly("spring.application.name=main");
		assertThat(project).textFile("src/main/resources/application-dev.properties")
			.lines()
			.containsExactly("spring.application.name=dev");
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
			TEST,
			MAIN, dev
			TEST, integration
			""")
	void shouldNotWriteFileWithoutProperties(SourceSet sourceSet, @Nullable String profile) throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		file(properties, sourceSet, profile);
		new ApplicationPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).filePaths()
			.containsOnly("src/main/resources/application.properties");
	}

	@Test
	void shouldAlwaysWriteMainDefaultFileEvenWhenOnlyProfilesHaveProperties() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.profile("dev").add("test", "value");
		new ApplicationPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).filePaths()
			.containsOnly("src/main/resources/application.properties", "src/main/resources/application-dev.properties");
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.isEmpty();
	}

	@Test
	void shouldResolvePathsThroughBuildSystem() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "main");
		properties.file(SourceSet.TEST).add("spring.application.name", "test");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new CustomLayoutBuildSystem());
		description.setLanguage(new JavaLanguage());
		new ApplicationPropertiesContributor(properties, description).contribute(this.directory);
		ProjectStructure project = new ProjectStructure(this.directory);
		assertThat(project).filePaths()
			.containsOnly("custom-main/resources/application.properties",
					"custom-test/resources/application.properties");
		assertThat(project).textFile("custom-main/resources/application.properties")
			.lines()
			.containsExactly("spring.application.name=main");
		assertThat(project).textFile("custom-test/resources/application.properties")
			.lines()
			.containsExactly("spring.application.name=test");
	}

	private static ApplicationPropertiesFile file(ApplicationProperties properties, SourceSet sourceSet,
			@Nullable String profile) {
		return (profile != null) ? properties.file(sourceSet, profile) : properties.file(sourceSet);
	}

	/**
	 * A {@link BuildSystem} with a non-standard layout, used to verify that the
	 * contributor resolves paths through {@link BuildSystem#getMainSource} and
	 * {@link BuildSystem#getTestSource} rather than hardcoding {@code src/main} and
	 * {@code src/test}.
	 */
	private static final class CustomLayoutBuildSystem implements BuildSystem {

		@Override
		public String id() {
			return "custom";
		}

		@Override
		public SourceStructure getMainSource(Path projectRoot, Language language) {
			return new SourceStructure(projectRoot.resolve("custom-main"), language);
		}

		@Override
		public SourceStructure getTestSource(Path projectRoot, Language language) {
			return new SourceStructure(projectRoot.resolve("custom-test"), language);
		}

	}

}
