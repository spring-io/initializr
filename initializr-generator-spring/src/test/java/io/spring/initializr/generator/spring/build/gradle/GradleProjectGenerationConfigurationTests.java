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
import java.util.Map;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleBuildWriter;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleProjectGenerationConfiguration} with Groovy DSL build system.
 *
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 */
class GradleProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
				.withConfiguration(BuildProjectGenerationConfiguration.class,
						GradleProjectGenerationConfiguration.class)
				.withDirectory(directory)
				.withBean(InitializrMetadata.class,
						() -> InitializrMetadataTestBuilder.withDefaults().build())
				.withDescriptionCustomizer((description) -> description
						.setBuildSystem(new GradleBuildSystem()));
	}

	static Stream<Arguments> supportedPlatformVersions() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE"),
				Arguments.arguments("2.0.6.RELEASE"),
				Arguments.arguments("2.1.3.RELEASE"));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("supportedPlatformVersions")
	void buildWriterIsContributed(String platformVersion) {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		BuildWriter buildWriter = this.projectTester.generate(description,
				(context) -> context.getBean(BuildWriter.class));
		assertThat(buildWriter).isInstanceOf(GradleBuildProjectContributor.class);
		assertThat(ReflectionTestUtils.getField(buildWriter, "buildWriter"))
				.isInstanceOf(GroovyDslGradleBuildWriter.class);
	}

	static Stream<Arguments> gradleWrapperParameters() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE", "3.5.1"),
				Arguments.arguments("2.0.6.RELEASE", "4.10.3"),
				Arguments.arguments("2.1.3.RELEASE", "5.4.1"));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("gradleWrapperParameters")
	void gradleWrapperIsContributedWhenGeneratingGradleProject(String platformVersion,
			String expectedGradleVersion) throws IOException {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		ProjectStructure projectStructure = this.projectTester.generate(description);
		List<String> relativePaths = projectStructure.getRelativePathsOfProjectFiles();
		assertThat(relativePaths).contains("gradlew", "gradlew.bat",
				"gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		try (Stream<String> lines = Files.lines(
				projectStructure.resolve("gradle/wrapper/gradle-wrapper.properties"))) {
			assertThat(lines.filter((line) -> line
					.contains(String.format("gradle-%s-bin.zip", expectedGradleVersion))))
							.hasSize(1);
		}
	}

	@Test
	void buildDotGradleIsContributedWhenGeneratingGradleProject() {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		description.setLanguage(new JavaLanguage("11"));
		description.addDependency("acme",
				Dependency.withCoordinates("com.example", "acme")
						.scope(DependencyScope.COMPILE).build());
		ProjectStructure projectStructure = this.projectTester.generate(description);
		List<String> relativePaths = projectStructure.getRelativePathsOfProjectFiles();
		assertThat(relativePaths).contains("build.gradle");
		List<String> lines = projectStructure.readAllLines("build.gradle");
		assertThat(lines).containsExactly("plugins {",
				"    id 'org.springframework.boot' version '2.1.0.RELEASE'",
				"    id 'java'", "}", "",
				"apply plugin: 'io.spring.dependency-management'", "",
				"group = 'com.example'", "version = '0.0.1-SNAPSHOT'",
				"sourceCompatibility = '11'", "", "repositories {", "    mavenCentral()",
				"}", "", "dependencies {",
				"    implementation 'org.springframework.boot:spring-boot-starter'",
				"    implementation 'com.example:acme'",
				"    testImplementation 'org.springframework.boot:spring-boot-starter-test'",
				"}");
	}

	@Test
	void warPluginIsAppliedWhenBuildingProjectThatUsesWarPackaging() throws IOException {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		description.setLanguage(new JavaLanguage());
		description.setPackaging(new WarPackaging());
		ProjectStructure projectStructure = this.projectTester.generate(description);
		List<String> relativePaths = projectStructure.getRelativePathsOfProjectFiles();
		assertThat(relativePaths).contains("build.gradle");
		try (Stream<String> lines = Files
				.lines(projectStructure.resolve("build.gradle"))) {
			assertThat(lines.filter((line) -> line.contains("    id 'war'"))).hasSize(1);
		}
	}

	@Test
	void junitPlatformIsConfiguredWithCompatibleVersion() {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.4.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("build.gradle");
		List<String> lines = projectStructure.readAllLines("build.gradle");
		assertThat(lines).containsSequence("test {", "    useJUnitPlatform()", "}");
	}

	@Test
	void junitPlatformIsNotConfiguredWithIncompatibleVersion() {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.4.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("build.gradle");
		List<String> lines = projectStructure.readAllLines("build.gradle");
		assertThat(lines).doesNotContainSequence("test {", "    useJUnitPlatform()", "}");
	}

	static Stream<Arguments> annotationProcessorScopeBuildParameters() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE", false),
				Arguments.arguments("2.0.6.RELEASE", true),
				Arguments.arguments("2.1.3.RELEASE", true));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("annotationProcessorScopeBuildParameters")
	void gradleAnnotationProcessorScopeCustomizerIsContributedIfNecessary(
			String platformVersion, boolean contributorExpected) {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		Map<String, GradleAnnotationProcessorScopeBuildCustomizer> generate = this.projectTester
				.generate(description, (context) -> context.getBeansOfType(
						GradleAnnotationProcessorScopeBuildCustomizer.class));
		assertThat(generate).hasSize((contributorExpected) ? 1 : 0);
	}

}
