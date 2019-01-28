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

package io.spring.initializr.generator;

import java.util.stream.Stream;

import io.spring.initializr.test.generator.ProjectAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

/**
 * Project generator tests for supported languages.
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorLanguageTests extends AbstractProjectGeneratorTests {

	public static Stream<Arguments> parameters() {
		return Stream.of(Arguments.arguments("java", "java"),
				Arguments.arguments("groovy", "groovy"),
				Arguments.arguments("kotlin", "kt"));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationJar(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);
		generateProject(request).isGenericProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, language, extension);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationWar(String language, String extension) {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(language);
		request.setPackaging("war");
		generateProject(request).isGenericWarProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, language, extension);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationMainClass(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(
				"src/main/" + language + "/com/example/demo/DemoApplication." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplication."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void previousGenerationMainClass(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);
		request.setBootVersion("1.5.18.RELEASE");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(
				"src/main/" + language + "/com/example/demo/DemoApplication." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/previous/"
						+ "/DemoApplication." + getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationTestClass(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + language
				+ "/com/example/demo/DemoApplicationTests." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTests."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationTestClassWeb(String language, String extension) {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + language
				+ "/com/example/demo/DemoApplicationTests." + extension)
				.equalsTo(new ClassPathResource(
						"project/" + language + "/standard/DemoApplicationTestsWeb."
								+ getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationServletInitializer(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);
		request.setPackaging("war");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/demo/ServletInitializer." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/standard/"
						+ "ServletInitializer." + getExpectedExtension(extension)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void previousGenerationServletInitializer(String language, String extension) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);
		request.setBootVersion("1.5.18.RELEASE");
		request.setPackaging("war");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + language
				+ "/com/example/demo/ServletInitializer." + extension)
				.equalsTo(new ClassPathResource("project/" + language + "/previous/"
						+ "ServletInitializer." + getExpectedExtension(extension)));
	}

	private String getExpectedExtension(String extension) {
		return extension + ".gen";
	}

}
