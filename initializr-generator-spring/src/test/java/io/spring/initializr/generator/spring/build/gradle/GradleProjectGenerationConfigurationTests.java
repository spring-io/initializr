/*
 * Copyright 2012-2024 the original author or authors.
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

import java.nio.file.Path;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleBuildWriter;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.build.BuildProjectGenerationConfiguration;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
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
 * @author Moritz Halbritter
 */
class GradleProjectGenerationConfigurationTests {

	private static final String GRADLE_VERSION = "8.13";

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
			.withConfiguration(BuildProjectGenerationConfiguration.class, GradleProjectGenerationConfiguration.class)
			.withDirectory(directory)
			.withBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build())
			.withDescriptionCustomizer((description) -> description.setBuildSystem(new GradleBuildSystem()));
	}

	static Stream<Arguments> supportedPlatformVersions() {
		return Stream.of(Arguments.arguments("3.3.0"), Arguments.arguments("3.4.0"));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("supportedPlatformVersions")
	void buildWriterIsContributed(String platformVersion) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		this.projectTester.configure(description, (context) -> {
			assertThat(context).hasSingleBean(BuildWriter.class)
				.getBean(BuildWriter.class)
				.isInstanceOf(GradleBuildProjectContributor.class);
			assertThat(ReflectionTestUtils.getField(context.getBean(BuildWriter.class), "buildWriter"))
				.isInstanceOf(GroovyDslGradleBuildWriter.class);
		});
	}

	static Stream<Arguments> gradleWrapperParameters() {
		return Stream.of(Arguments.arguments("3.3.0", GRADLE_VERSION), Arguments.arguments("3.4.0", GRADLE_VERSION));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("gradleWrapperParameters")
	void gradleWrapperIsContributedWhenGeneratingGradleProject(String platformVersion, String expectedGradleVersion) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).containsFiles("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		assertThat(project).textFile("gradle/wrapper/gradle-wrapper.properties")
			.containsOnlyOnce(String.format("gradle-%s-bin.zip", expectedGradleVersion));
	}

	@Test
	void buildDotGradleIsContributedWhenGeneratingGradleProject() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		description.setLanguage(new JavaLanguage("11"));
		description.addDependency("acme",
				Dependency.withCoordinates("com.example", "acme").scope(DependencyScope.COMPILE));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").containsExactly(
		// @formatter:off
				"plugins {",
				"    id 'java'",
				"    id 'org.springframework.boot' version '2.4.0'",
				"    id 'io.spring.dependency-management' version '1.0.6.RELEASE'",
				"}",
				"",
				"group = 'com.example'",
				"version = '0.0.1-SNAPSHOT'",
				"",
				"java {",
				"    toolchain {",
				"        languageVersion = JavaLanguageVersion.of(11)",
				"    }",
				"}",
				"",
				"repositories {",
				"    mavenCentral()",
				"}",
				"",
				"dependencies {",
				"    implementation 'org.springframework.boot:spring-boot-starter'",
				"    implementation 'com.example:acme'",
				"    testImplementation 'org.springframework.boot:spring-boot-starter-test'",
				"    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'",
				"}",
				"",
				"tasks.named('test') {",
				"    useJUnitPlatform()",
				"}"); // @formatter:on
	}

	@Test
	void groovyPluginIsAppliedWhenBuildingProjectThatUsesGroovyLanguage() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0.RELEASE"));
		description.setLanguage(new GroovyLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").lines().containsOnlyOnce("    id 'groovy'");
	}

	@Test
	void warPluginIsAppliedWhenBuildingProjectThatUsesWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0.RELEASE"));
		description.setLanguage(new JavaLanguage());
		description.setPackaging(new WarPackaging());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").lines().containsOnlyOnce("    id 'war'");
	}

	@Test
	void junitPlatformIsConfiguredWithCompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.4.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle")
			.lines()
			.containsSequence("tasks.named('test') {", "    useJUnitPlatform()", "}");
	}

	@Test
	void testStarterExcludesVintageEngineWithCompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.4.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle")
			.lines()
			.containsSequence("    testImplementation('org.springframework.boot:spring-boot-starter-test') {",
					"        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'", "    }");
	}

	@Test
	void testStarterDoesNotExcludeVintageEngineWith24Snapshot() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0-SNAPSHOT"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").doesNotContain("exclude group");
	}

	@Test
	void testStarterDoesNotExcludeVintageEngineWith24Milestone() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0-M1"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").doesNotContain("exclude group");
	}

	@Test
	void gradleAnnotationProcessorScopeCustomizerIsContributed() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		description.setLanguage(new JavaLanguage());
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(GradleAnnotationProcessorScopeBuildCustomizer.class));
	}

}
