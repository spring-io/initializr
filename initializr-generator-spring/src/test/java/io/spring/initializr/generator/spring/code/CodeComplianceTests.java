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
import io.spring.initializr.generator.spring.test.ProjectAssert;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

/**
 * Code compliance tests.
 *
 * @author Stephane Nicoll
 */
class CodeComplianceTests extends AbstractComplianceTests {

	private static final BuildSystem maven = BuildSystem.forId(MavenBuildSystem.ID);

	static Stream<Arguments> parameters() {
		return Stream.of(Arguments.arguments(new JavaLanguage(), "java"),
				Arguments.arguments(new GroovyLanguage(), "groovy"),
				Arguments.arguments(new KotlinLanguage(), "kt"));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationJar(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE");
		project.isGenericProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, language.id(), extension);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationWar(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> description.setPackaging(Packaging.forId("war")));
		project.isGenericProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, language.id(), extension);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationMainClass(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE");
		project.sourceCodeAssert(
				"src/main/" + language + "/com/example/demo/DemoApplication." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplication."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void previousGenerationMainClass(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> description
						.setPlatformVersion(Version.parse("1.5.18.RELEASE")));
		project.sourceCodeAssert(
				"src/main/" + language + "/com/example/demo/DemoApplication." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/previous/"
						+ "/DemoApplication." + getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationTestClass(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE");
		project.sourceCodeAssert("src/test/" + language
				+ "/com/example/demo/DemoApplicationTests." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTests."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationTestClassWeb(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> description.addDependency("web",
						MetadataBuildItemMapper.toDependency(WEB)));
		project.sourceCodeAssert("src/test/" + language
				+ "/com/example/demo/DemoApplicationTests." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTestsWeb."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationServletInitializer(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> description.setPackaging(Packaging.forId("war")));
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/demo/ServletInitializer." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/standard/"
						+ "ServletInitializer." + getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void previousGenerationServletInitializer(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> {
					description.setPackaging(Packaging.forId("war"));
					description.setPlatformVersion(Version.parse("1.5.18.RELEASE"));
				});
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/demo/ServletInitializer." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/previous/"
						+ "ServletInitializer." + getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void currentGenerationCustomCoordinates(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "2.1.1.RELEASE",
				(description) -> {
					description.setGroupId("com.example.acme");
					description.setArtifactId("my-project");
					description.setPackageName("com.example.acme.myproject");
					description.setApplicationName("MyProjectApplication");
				});
		project.isGenericProject("com.example.acme.myproject", "MyProjectApplication",
				language.id(), extension);
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/acme/myproject/MyProjectApplication." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/MyProjectApplication."
								+ getExpectedExtension(extension)));
		project.sourceCodeAssert("src/test/" + language
				+ "/com/example/acme/myproject/MyProjectApplicationTests." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/MyProjectApplicationTests."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void previousGenerationCustomCoordinates(Language language, String extension) {
		ProjectAssert project = generateProject(language, maven, "1.5.18.RELEASE",
				(description) -> {
					description.setGroupId("com.example.acme");
					description.setArtifactId("my-project");
					description.setPackageName("com.example.acme.myproject");
					description.setApplicationName("MyProjectApplication");
				});
		project.isGenericProject("com.example.acme.myproject", "MyProjectApplication",
				language.id(), extension);
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/acme/myproject/MyProjectApplication." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/previous/MyProjectApplication."
								+ getExpectedExtension(extension)));
	}

	private String getExpectedExtension(String extension) {
		return extension + ".gen";
	}

}
