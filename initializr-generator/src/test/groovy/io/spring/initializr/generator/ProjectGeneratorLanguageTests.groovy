/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.generator

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import org.springframework.core.io.ClassPathResource

import static io.spring.initializr.test.generator.ProjectAssert.DEFAULT_APPLICATION_NAME
import static io.spring.initializr.test.generator.ProjectAssert.DEFAULT_PACKAGE_NAME

/**
 * Project generator tests for supported languages.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
class ProjectGeneratorLanguageTests extends AbstractProjectGeneratorTests {

	@Parameterized.Parameters(name = "{0}")
	static Object[] parameters() {
		Object[] java = ["java", "java"]
		Object[] groovy = ["groovy", "groovy"]
		Object[] kotlin = ["kotlin", "kt"]
		Object[] parameters = [java, groovy, kotlin]
		parameters
	}

	private final String language
	private final String extension
	private final String expectedExtension

	ProjectGeneratorLanguageTests(String language, String extension) {
		this.language = language
		this.extension = extension
		this.expectedExtension = extension + '.gen'
	}

	@Test
	void standardJar() {
		def request = createProjectRequest()
		request.language = language
		generateProject(request).isGenericProject(DEFAULT_PACKAGE_NAME, DEFAULT_APPLICATION_NAME,
				language, extension)
	}

	@Test
	void standardWar() {
		def request = createProjectRequest('web')
		request.language = language
		request.packaging = 'war'
		generateProject(request).isGenericWarProject(DEFAULT_PACKAGE_NAME, DEFAULT_APPLICATION_NAME,
				language, extension)
	}

	@Test
	void standardMainClass() {
		def request = createProjectRequest()
		request.language = language

		def project = generateProject(request)
		project.sourceCodeAssert("src/main/$language/com/example/DemoApplication.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplication.$expectedExtension"))
	}

	@Test
	void standardTestClass() {
		def request = createProjectRequest()
		request.language = language

		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/example/DemoApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	void standardTestClassWeb() {
		def request = createProjectRequest('web')
		request.language = language

		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/example/DemoApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTestsWeb.$expectedExtension"))
	}

	@Test
	void standardServletInitializer() {
		testServletInitializr(null, 'standard')
	}

	@Test
	void springBoot14M2ServletInitializer() {
		testServletInitializr('1.4.0.M2', 'standard')
	}

	@Test
	void springBoot14ServletInitializer() {
		testServletInitializr('1.4.0.M3', 'spring-boot-1.4')
	}

	private void testServletInitializr(String bootVersion, String expectedOutput) {
		def request = createProjectRequest()
		request.language = language
		request.packaging = 'war'
		if (bootVersion) {
			request.bootVersion = bootVersion
		}
		def project = generateProject(request)
		project.sourceCodeAssert("src/main/$language/com/example/ServletInitializer.$extension")
				.equalsTo(new ClassPathResource("project/$language/$expectedOutput/ServletInitializer.$expectedExtension"))
	}

	@Test
	void springBoot14M1TestClass() {
		def request = createProjectRequest()
		request.language = language
		request.bootVersion = '1.4.0.M1'

		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/example/DemoApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/standard/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	void springBoot14TestClass() {
		def request = createProjectRequest()
		request.language = language
		request.bootVersion = '1.4.0.M2'

		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/example/DemoApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/spring-boot-1.4/DemoApplicationTests.$expectedExtension"))
	}

	@Test
	void springBoot14TestClassWeb() {
		def request = createProjectRequest('web')
		request.language = language
		request.bootVersion = '1.4.0.M2'

		def project = generateProject(request)
		project.sourceCodeAssert("src/test/$language/com/example/DemoApplicationTests.$extension")
				.equalsTo(new ClassPathResource("project/$language/spring-boot-1.4/DemoApplicationTests.$expectedExtension"))
	}

}
