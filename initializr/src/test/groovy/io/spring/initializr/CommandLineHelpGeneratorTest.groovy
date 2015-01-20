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
		assertThat content, containsString('id-a - depA: and some description')
		assertThat content, containsString('id-b - depB')
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
		assertThat content, containsString('foo - foo-desc')
	}

	@Test
	void generateCurlCapabilities() {
		def metadata = InitializrMetadataBuilder.withDefaults().addDependencyGroup("test",
				createDependency('id-b', 'depB'),
				createDependency('id-a', 'depA', 'and some description')).validateAndGet()
		String content = generator.generateCurlCapabilities(metadata, "https://fake-service")
		assertThat content, containsString('id-a - depA: and some description')
		assertThat content, containsString('id-b - depB')
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
		assertThat content, containsString('id-a - depA: and some description')
		assertThat content, containsString('id-b - depB')
		assertThat content, containsString("https://fake-service")
		assertThat content, containsString('Examples:')
		assertThat content, not(containsString('curl'))
		assertThat content, containsString("http https://fake-service")
	}

	private static def createDependency(String id, String name) {
		createDependency(id, name, null)
	}

	private static def createDependency(String id, String name, String description) {
		new InitializrMetadata.Dependency(id: id, name: name, description: description)
	}

}
