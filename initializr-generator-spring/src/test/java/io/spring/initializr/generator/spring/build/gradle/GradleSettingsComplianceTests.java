/*
 * Copyright 2012 - present the original author or authors.
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

import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSettings;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.spring.AbstractComplianceTests;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gradle settings compliance tests.
 *
 * @author Sijun Yang
 */
class GradleSettingsComplianceTests extends AbstractComplianceTests {

	static Stream<Arguments> parameters() {
		return Stream.of(new JavaLanguage(), new GroovyLanguage(), new KotlinLanguage())
			.flatMap((language) -> Stream.of(
					Arguments.of(language, BuildSystem.forId(GradleBuildSystem.ID), "settings.gradle"),
					Arguments.of(language,
							BuildSystem.forIdAndDialect(GradleBuildSystem.ID, GradleBuildSystem.DIALECT_KOTLIN),
							"settings.gradle.kts")));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void defaultProjectSettings(Language language, BuildSystem build, String fileName) {
		ProjectStructure project = generateProject(language, build, "2.7.0");
		String path = "project/gradle/" + getAssertFileName(fileName);
		assertThat(project).textFile(fileName).as("Resource " + path).hasSameContentAs(new ClassPathResource(path));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void customArtifactId(Language language, BuildSystem build, String fileName) {
		ProjectStructure project = generateProject(language, build, "2.7.0",
				(description) -> description.setArtifactId("my-project"));
		String path = "project/gradle/custom-artifact-id-" + getAssertFileName(fileName);
		assertThat(project).textFile(fileName).as("Resource " + path).hasSameContentAs(new ClassPathResource(path));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void pluginRepository(Language language, BuildSystem build, String fileName) {
		ProjectStructure project = generateProject(language, build, "2.7.0", (description) -> {
		}, (context) -> context.registerBean(BuildCustomizer.class,
				() -> (gradleBuild) -> gradleBuild.pluginRepositories()
					.add(MavenRepository.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone"))));
		String path = "project/gradle/plugin-repository-" + getAssertFileName(fileName);
		assertThat(project).textFile(fileName).as("Resource " + path).hasSameContentAs(new ClassPathResource(path));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void pluginMapping(Language language, BuildSystem build, String fileName) {
		ProjectStructure project = generateProject(language, build, "2.7.0", (description) -> {
		}, (context) -> context.registerBean(BuildCustomizer.class,
				() -> (gradleBuild) -> ((GradleBuildSettings.Builder) gradleBuild.settings()).mapPlugin("com.example",
						Dependency.withCoordinates("com.example", "gradle-plugin")
							.version(VersionReference.ofValue("1.0.0"))
							.build())));
		String path = "project/gradle/plugin-mapping-" + getAssertFileName(fileName);
		assertThat(project).textFile(fileName).as("Resource " + path).hasSameContentAs(new ClassPathResource(path));
	}

	private String getAssertFileName(String fileName) {
		return fileName + ".gen";
	}

}
