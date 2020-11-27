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

package io.spring.initializr.generator.spring.code;

import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.spring.AbstractComplianceTests;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Code compliance tests.
 *
 * @author Stephane Nicoll
 */
class CodeComplianceTests extends AbstractComplianceTests {

	private static final BuildSystem maven = BuildSystem.forId(MavenBuildSystem.ID);

	static Stream<Arguments> parameters() {
		return Stream.of(Arguments.arguments(new JavaLanguage(), "java"),
				Arguments.arguments(new GroovyLanguage(), "groovy"), Arguments.arguments(new KotlinLanguage(), "kt"));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationJar(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1");
		assertThat(project).filePaths().contains(
				String.format("src/main/%s/com/example/demo/DemoApplication.%s", language.id(),
						language.sourceFileExtension()),
				String.format("src/test/%s/com/example/demo/DemoApplicationTests.%s", language.id(),
						language.sourceFileExtension()),
				"src/main/resources/application.properties");
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationWar(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1",
				(description) -> description.setPackaging(Packaging.forId("war")));
		assertThat(project).filePaths().contains(
				String.format("src/main/%s/com/example/demo/DemoApplication.%s", language.id(),
						language.sourceFileExtension()),
				String.format("src/test/%s/com/example/demo/DemoApplicationTests.%s", language.id(),
						language.sourceFileExtension()),
				"src/main/resources/application.properties");
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationMainClass(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1");
		assertThat(project).asJvmModule(language).mainSource("com.example.demo", "DemoApplication")
				.hasSameContentAs(new ClassPathResource(
						"project/" + language + "/standard/DemoApplication." + getExpectedExtension(language)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationTestClass(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1");
		assertThat(project).asJvmModule(language).testSource("com.example.demo", "DemoApplicationTests")
				.hasSameContentAs(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTests." + getExpectedExtension(language)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationTestClassWeb(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1",
				(description) -> description.addDependency("web", MetadataBuildItemMapper.toDependency(WEB)));
		assertThat(project).asJvmModule(language).testSource("com.example.demo", "DemoApplicationTests")
				.hasSameContentAs(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTestsWeb." + getExpectedExtension(language)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationServletInitializer(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1",
				(description) -> description.setPackaging(Packaging.forId("war")));
		assertThat(project).asJvmModule(language).mainSource("com.example.demo", "ServletInitializer")
				.hasSameContentAs(new ClassPathResource(
						"project/" + language + "/standard/" + "ServletInitializer." + getExpectedExtension(language)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationCustomCoordinates(Language language) {
		ProjectStructure project = generateProject(language, maven, "2.4.1", (description) -> {
			description.setGroupId("com.example.acme");
			description.setArtifactId("my-project");
			description.setPackageName("com.example.acme.myproject");
			description.setApplicationName("MyProjectApplication");
		});
		assertThat(project).asJvmModule(language).mainSource("com.example.acme.myproject", "MyProjectApplication")
				.hasSameContentAs(new ClassPathResource(
						"project/" + language + "/standard/MyProjectApplication." + getExpectedExtension(language)));
		assertThat(project).asJvmModule(language).testSource("com.example.acme.myproject", "MyProjectApplicationTests")
				.hasSameContentAs(new ClassPathResource("project/" + language + "/standard/MyProjectApplicationTests."
						+ getExpectedExtension(language)));
	}

	private String getExpectedExtension(Language language) {
		return language.sourceFileExtension() + ".gen";
	}

}
