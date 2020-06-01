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

package io.spring.initializr.generator.spring.build.gradle;

import java.nio.file.Path;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleBuildWriter;
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
 */
class GradleProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
				.withConfiguration(BuildProjectGenerationConfiguration.class,
						GradleProjectGenerationConfiguration.class)
				.withDirectory(directory)
				.withBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build())
				.withDescriptionCustomizer((description) -> description.setBuildSystem(new GradleBuildSystem()));
	}

	static Stream<Arguments> supportedPlatformVersions() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE"), Arguments.arguments("2.1.3.RELEASE"),
				Arguments.arguments("2.2.3.RELEASE"));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("supportedPlatformVersions")
	void buildWriterIsContributed(String platformVersion) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		this.projectTester.configure(description, (context) -> {
			assertThat(context).hasSingleBean(BuildWriter.class).getBean(BuildWriter.class)
					.isInstanceOf(GradleBuildProjectContributor.class);
			assertThat(ReflectionTestUtils.getField(context.getBean(BuildWriter.class), "buildWriter"))
					.isInstanceOf(GroovyDslGradleBuildWriter.class);
		});
	}

	static Stream<Arguments> gradleWrapperParameters() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE", "3.5.1"), Arguments.arguments("2.0.6.RELEASE", "4.10.3"),
				Arguments.arguments("2.1.3.RELEASE", "5.6.4"), Arguments.arguments("2.2.3.RELEASE", "6.4.1"));
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
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		description.setLanguage(new JavaLanguage("11"));
		description.addDependency("acme",
				Dependency.withCoordinates("com.example", "acme").scope(DependencyScope.COMPILE));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").containsExactly("plugins {",
				"    id 'org.springframework.boot' version '2.1.0.RELEASE'",
				"    id 'io.spring.dependency-management' version '1.0.6.RELEASE'", "    id 'java'", "}", "",
				"group = 'com.example'", "version = '0.0.1-SNAPSHOT'", "sourceCompatibility = '11'", "",
				"repositories {", "    mavenCentral()", "}", "", "dependencies {",
				"    implementation 'org.springframework.boot:spring-boot-starter'",
				"    implementation 'com.example:acme'",
				"    testImplementation 'org.springframework.boot:spring-boot-starter-test'", "}");
	}

	@Test
	void warPluginIsAppliedWhenBuildingProjectThatUsesWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
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
		assertThat(project).textFile("build.gradle").lines().containsSequence("test {", "    useJUnitPlatform()", "}");
	}

	@Test
	void junitPlatformIsNotConfiguredWithIncompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.4.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").lines().doesNotContainSequence("test {", "    useJUnitPlatform()",
				"}");
	}

	@Test
	@Deprecated
	void testStarterExcludesVintageEngineAndJUnitWithAppropriateVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.M4"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").lines().containsSequence(
				"    testImplementation('org.springframework.boot:spring-boot-starter-test') {",
				"        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'",
				"        exclude group: 'junit', module: 'junit'", "    }");
	}

	@Test
	void testStarterExcludesVintageEngineWithCompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.M5"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").lines().containsSequence(
				"    testImplementation('org.springframework.boot:spring-boot-starter-test') {",
				"        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'", "    }");
	}

	@Test
	void testStarterDoesNotExcludesVintageEngineAndJUnitWithIncompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.6.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("build.gradle").doesNotContain("exclude group");
	}

	static Stream<Arguments> annotationProcessorScopeBuildParameters() {
		return Stream.of(Arguments.arguments("1.5.17.RELEASE", false), Arguments.arguments("2.0.6.RELEASE", true),
				Arguments.arguments("2.1.3.RELEASE", true));
	}

	@ParameterizedTest(name = "Spring Boot {0}")
	@MethodSource("annotationProcessorScopeBuildParameters")
	void gradleAnnotationProcessorScopeCustomizerIsContributedIfNecessary(String platformVersion,
			boolean contributorExpected) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse(platformVersion));
		description.setLanguage(new JavaLanguage());
		this.projectTester.configure(description, (context) -> assertThat(context)
				.getBeans(GradleAnnotationProcessorScopeBuildCustomizer.class).hasSize((contributorExpected) ? 1 : 0));
	}

}
