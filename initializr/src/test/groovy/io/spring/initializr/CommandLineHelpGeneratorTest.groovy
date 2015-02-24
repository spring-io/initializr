/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr

import io.spring.initializr.test.InitializrMetadataBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertThat

/**
 * @author Stephane Nicoll
 */
class CommandLineHelpGeneratorTest {

	private CommandLineHelpGenerator generator = new CommandLineHelpGenerator()

	@Test
	void generateGenericCapabilities() {
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).validateAndGet()
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
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addType(new InitializrMetadata.Type(id: 'foo', name: 'foo-name', description: 'foo-desc'))
				.validateAndGet()
		String content = generator.generateGenericCapabilities(metadata, "https://fake-service")
		assertCommandLineCapabilities(content)
		assertThat content, containsString('| foo')
		assertThat content, containsString('| foo-desc')
	}

	@Test
	void generateCurlCapabilities() {
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).validateAndGet()
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
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).validateAndGet()
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
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).validateAndGet()
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
		InitializrMetadata.Dependency first = new InitializrMetadata.Dependency(
				id: 'first', description: 'first desc', versionRange: '1.2.0.RELEASE')
		InitializrMetadata.Dependency second = new InitializrMetadata.Dependency(
				id: 'second', description: 'second desc', versionRange: ' [1.2.0.RELEASE,1.3.0.M1)  ')
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test", first, second).validateAndGet()
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
		new InitializrMetadata.Dependency(id: id, name: name, description: description)
	}

}
