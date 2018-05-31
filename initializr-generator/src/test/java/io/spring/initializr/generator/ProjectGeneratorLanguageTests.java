/*
 * Copyright 2012-2018 the original author or authors.
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

import io.spring.initializr.test.generator.ProjectAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.springframework.core.io.ClassPathResource;

/**
 * Project generator tests for supported languages.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
public class ProjectGeneratorLanguageTests extends AbstractProjectGeneratorTests {

	@Parameterized.Parameters(name = "{0}")
	public static Object[] parameters() {
		Object[] java = new Object[] { "java", "java" };
		Object[] groovy = new Object[] { "groovy", "groovy" };
		Object[] kotlin = new Object[] { "kotlin", "kt" };
		return new Object[] { java, groovy, kotlin };
	}

	private final String language;

	private final String extension;

	private final String expectedExtension;

	public ProjectGeneratorLanguageTests(String language, String extension) {
		this.language = language;
		this.extension = extension;
		this.expectedExtension = extension + ".gen";
	}

	@Test
	public void standardJar() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		generateProject(request).isGenericProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, this.language, this.extension);
	}

	@Test
	public void standardWar() {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(this.language);
		request.setPackaging("war");
		generateProject(request).isGenericWarProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, this.language, this.extension);
	}

	@Test
	public void standardMainClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/DemoApplication." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplication." + this.expectedExtension));
	}

	@Test
	public void standardTestClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplicationTests." + this.expectedExtension));
	}

	@Test
	public void standardTestClassWeb() {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplicationTestsWeb." + this.expectedExtension));
	}

	@Test
	public void standardServletInitializer() {
		testServletInitializr(null, "standard");
	}

	@Test
	public void springBoot14M2ServletInitializer() {
		testServletInitializr("1.4.0.M2", "standard");
	}

	@Test
	public void springBoot14ServletInitializer() {
		testServletInitializr("1.4.0.M3", "spring-boot-1.4");
	}

	@Test
	public void springBoot2ServletInitializer() {
		testServletInitializr("2.0.0.M3", "spring-boot-2.0");
	}

	private void testServletInitializr(String bootVersion, String expectedOutput) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setPackaging("war");
		if (bootVersion != null) {
			request.setBootVersion(bootVersion);
		}
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/ServletInitializer." + this.extension)
				.equalsTo(new ClassPathResource(
						"project/" + this.language + "/" + expectedOutput
								+ "/ServletInitializer." + this.expectedExtension));
	}

	@Test
	public void springBoot14M1TestClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setBootVersion("1.4.0.M1");

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplicationTests." + this.expectedExtension));
	}

	@Test
	public void springBoot14TestClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setBootVersion("1.4.0.M2");

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/spring-boot-1.4/DemoApplicationTests."
						+ this.expectedExtension));
	}

	@Test
	public void springBoot14TestClassWeb() {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(this.language);
		request.setBootVersion("1.4.0.M2");

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/spring-boot-1.4/DemoApplicationTests."
						+ this.expectedExtension));
	}

}
