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

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
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
	static Object[] parameters() {
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
	void standardJarJava() {
		testStandardJar('java')
	}

	@Test
	void standardJarGroovy() {
		testStandardJar('groovy')
	}

	@Test
	void standardJarKotlin() {
		testStandardJar('kotlin')
	}

	private void testStandardJar(def language) {
		def request = createProjectRequest()
		request.language = language
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/standard/$assertFileName"))
	}

	@Test
	void standardWarJava() {
		testStandardWar('java')
	}

	@Test
	void standardWarGroovy() {
		testStandardWar('java')
	}

	@Test
	void standardWarKotlin() {
		testStandardWar('kotlin')
	}

	private void testStandardWar(def language) {
		def request = createProjectRequest('web')
		request.packaging = 'war'
		request.language = language
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$language/war/$assertFileName"))
	}

	@Test
	void versionOverride() {
		def request = createProjectRequest('web')
		request.buildProperties.versions['spring-foo.version'] = {'0.1.0.RELEASE'}
		request.buildProperties.versions['spring-bar.version'] = {'0.2.0.RELEASE'}
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$build/version-override-$assertFileName"))
	}

	@Test
	void bomWithVersionProperty() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'the-bom')
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom',
				version: '1.3.3', versionProperty: 'foo.version')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('the-bom', bom).build()
		applyMetadata(metadata)
		def request = createProjectRequest('foo')
		def project = generateProject(request)
		project.sourceCodeAssert("$fileName")
				.equalsTo(new ClassPathResource("project/$build/bom-property-$assertFileName"))
	}

	@Override
	ProjectRequest createProjectRequest(String... styles) {
		def request = super.createProjectRequest(styles)
		request.type = "$build-project"
		request
	}

}
