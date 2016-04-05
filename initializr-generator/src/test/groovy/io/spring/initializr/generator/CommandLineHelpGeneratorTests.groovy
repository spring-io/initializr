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

import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.Type
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

/**
 * @author Stephane Nicoll
 */
class CommandLineHelpGeneratorTests {

	private CommandLineHelpGenerator generator = new CommandLineHelpGenerator()

	@Test
	void generateGenericCapabilities() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).build()
		String content = generator.generateGenericCapabilities(metadata, "https://fake-service")
		assertCommandLineCapabilities(content)
		assertThat content, containsString('id-a | and some description |')
		assertThat content, containsString('id-b | depB')
		assertThat content, containsString("https://fake-service")
		assertThat content, not(containsString('Examples:'))
		assertThat content, not(containsString('curl'))
	}

	@Test
	void generateCapabilitiesWithTypeDescription() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addType(new Type(id: 'foo', name: 'foo-name', description: 'foo-desc'))
				.build()
		String content = generator.generateGenericCapabilities(metadata, "https://fake-service")
		assertCommandLineCapabilities(content)
		assertThat content, containsString('| foo')
		assertThat content, containsString('| foo-desc')
	}

	@Test
	void generateCurlCapabilities() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).build()
		String content = generator.generateCurlCapabilities(metadata, "https://fake-service")
		assertCommandLineCapabilities(content)
		assertThat content, containsString('id-a | and some description |')
		assertThat content, containsString('id-b | depB')
		assertThat content, containsString("https://fake-service")
		assertThat content, containsString('Examples:')
		assertThat content, containsString('curl')
	}

	@Test
	void generateHttpCapabilities() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).build()
		String content = generator.generateHttpieCapabilities(metadata, "https://fake-service")
		assertCommandLineCapabilities(content)
		assertThat content, containsString('id-a | and some description |')
		assertThat content, containsString('id-b | depB')
		assertThat content, containsString("https://fake-service")
		assertThat content, containsString('Examples:')
		assertThat content, not(containsString('curl'))
		assertThat content, containsString("http https://fake-service")
	}

	@Test
	void generateSpringBootCliCapabilities() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).build()
		String content = generator.generateSpringBootCliCapabilities(metadata, "https://fake-service")
		assertThat content, containsString("| Id")
		assertThat content, containsString("| Tags")
		assertThat content, containsString('id-a | and some description |')
		assertThat content, containsString('id-b | depB')
		assertThat content, containsString("https://fake-service")
		assertThat content, not(containsString('Examples:'))
		assertThat content, not(containsString('curl'))
		assertThat content, not(containsString('| Rel'))
		assertThat content, not(containsString("| dependencies"))
		assertThat content, not(containsString("| applicationName"))
		assertThat content, not(containsString("| baseDir"))
	}

	@Test
	void generateCapabilitiesWithVersionRange() {
		def first = new Dependency(id: 'first', description: 'first desc', versionRange: '1.2.0.RELEASE')
		def second = new Dependency(id: 'second', description: 'second desc', versionRange: ' [1.2.0.RELEASE,1.3.0.M1)  ')
		def metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test", first, second).build()
		String content = generator.generateSpringBootCliCapabilities(metadata, "https://fake-service")
		assertThat content, containsString('| first  | first desc  | >= 1.2.0.RELEASE         |')
		assertThat content, containsString('| second | second desc | [1.2.0.RELEASE,1.3.0.M1) |')
	}

	private assertCommandLineCapabilities(String content) {
		assertThat content, containsString("| Rel")
		assertThat content, containsString("| dependencies")
		assertThat content, containsString("| applicationName")
		assertThat content, containsString("| baseDir")
		assertThat content, not(containsString('| Tags'))
	}

	private static def createDependency(String id, String name) {
		createDependency(id, name, null)
	}

	private static def createDependency(String id, String name, String description) {
		new Dependency(id: id, name: name, description: description)
	}

}
