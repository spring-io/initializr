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

	@Parameterized.Parameters(name = "{0}")
	public static Object[] parameters() {
		Object[] maven = ["maven", "pom.xml"]
		Object[] gradle = ["gradle", "build.gradle"]
		Object[] parameters = [maven, gradle]
		parameters
	}

	private final String build
	private final String fileName
	private final String assertFileName

	ProjectGeneratorBuildTests(String build, String fileName) {
		this.build = build
		this.fileName = fileName
		this.assertFileName = fileName + ".gen"
	}

	@Test
	public void standardJarJava() {
		testStandardJar('java')
	}

	@Test
	public void standardJarGroovy() {
		testStandardJar('groovy')
	}

	@Test
	public void standardJarKotlin() {
		testStandardJar('kotlin')
	}

	private void testStandardJar(def language) {
		def request = createProjectRequest()
		request.language = language
		request.type = "$build-project"
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/standard/$assertFileName"))
	}

	@Test
	public void standardWarJava() {
		testStandardWar('java')
	}

	@Test
	public void standardWarGroovy() {
		testStandardWar('java')
	}

	@Test
	public void standardWarKotlin() {
		testStandardWar('kotlin')
	}

	private void testStandardWar(def language) {
		def request = createProjectRequest('web')
		request.packaging = 'war'
		request.language = language
		request.type = "$build-project"
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/war/$assertFileName"))
	}

	@Test
	public void versionOverride() {
		def request = createProjectRequest('web')
		request.type = "$build-project"
		request.buildProperties.versions['spring-foo.version'] = {'0.1.0.RELEASE'}
		request.buildProperties.versions['spring-bar.version'] = {'0.2.0.RELEASE'}
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$build/version-override-$assertFileName"))
	}

}
