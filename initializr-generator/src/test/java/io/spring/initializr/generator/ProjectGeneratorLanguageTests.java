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
	public void currentGenerationJar() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		generateProject(request).isGenericProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, this.language, this.extension);
	}

	@Test
	public void currentGenerationWar() {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(this.language);
		request.setPackaging("war");
		generateProject(request).isGenericWarProject(ProjectAssert.DEFAULT_PACKAGE_NAME,
				ProjectAssert.DEFAULT_APPLICATION_NAME, this.language, this.extension);
	}

	@Test
	public void currentGenerationMainClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/DemoApplication." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplication." + this.expectedExtension));
	}

	@Test
	public void previousGenerationMainClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setBootVersion("1.5.18.RELEASE");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/DemoApplication." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language + "/previous/"
						+ "/DemoApplication." + this.expectedExtension));
	}

	@Test
	public void currentGenerationTestClass() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplicationTests." + this.expectedExtension));
	}

	@Test
	public void currentGenerationTestClassWeb() {
		ProjectRequest request = createProjectRequest("web");
		request.setLanguage(this.language);

		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/test/" + this.language
				+ "/com/example/demo/DemoApplicationTests." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language
						+ "/standard/DemoApplicationTestsWeb." + this.expectedExtension));
	}

	@Test
	public void currentGenerationServletInitializer() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setPackaging("war");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/ServletInitializer." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language + "/standard/"
						+ "ServletInitializer." + this.expectedExtension));
	}

	@Test
	public void previousGenerationServletInitializer() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(this.language);
		request.setBootVersion("1.5.18.RELEASE");
		request.setPackaging("war");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/" + this.language
				+ "/com/example/demo/ServletInitializer." + this.extension)
				.equalsTo(new ClassPathResource("project/" + this.language + "/previous/"
						+ "ServletInitializer." + this.expectedExtension));
	}

}
