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
 * Project generator tests for supported build systems.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
class ProjectGeneratorBuildTests extends AbstractProjectGeneratorTests {

	@Parameterized.Parameters(name = "{0} with {1}")
	public static Object[] parameters() {
		Object[] javaMaven = ["java", "maven", "pom.xml"]
		Object[] javaGradle = ["java", "gradle", "build.gradle"]
		Object[] groovyMaven = ["groovy", "maven", "pom.xml"]
		Object[] groovyGradle = ["groovy", "gradle", "build.gradle"]
		Object[] kotlinMaven = ["kotlin", "maven", "pom.xml"]
		Object[] kotlinGradle = ["kotlin", "gradle", "build.gradle"]
		Object[] parameters = [javaMaven, javaGradle, groovyMaven, groovyGradle, kotlinMaven, kotlinGradle]
		parameters
	}

	private final String language
	private final String build
	private final String fileName
	private final String assertFileName

	ProjectGeneratorBuildTests(String language, String build, String fileName) {
		this.language = language
		this.build = build
		this.fileName = fileName
		this.assertFileName = fileName + ".gen"
	}

	@Test
	public void standardJar() {
		def request = createProjectRequest()
		request.language = language
		request.type = "$build-project"
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/standard/$assertFileName"))
	}

	@Test
	public void standardWar() {
		def request = createProjectRequest('web')
		request.packaging = 'war'
		request.language = language
		request.type = "$build-project"
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/war/$assertFileName"))
	}

}
