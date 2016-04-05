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

package io.spring.initializr.web.support

import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.InitializrMetadataBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

/**
 * @author Stephane Nicoll
 */
class SpringBootMetadataReaderTests {

	private final InitializrMetadata metadata = InitializrMetadataBuilder.create().build()

	@Test
	void readAvailableVersions() {
		def versions = new SpringBootMetadataReader(metadata.configuration.env.springBootMetadataUrl).bootVersions
		assertNotNull "spring boot versions should not be null", versions
		boolean defaultFound
		versions.each {
			assertNotNull 'Id must be set', it.id
			assertNotNull 'Name must be set', it.name
			if (it.default) {
				if (defaultFound) {
					fail("One default version was already found $it.id")
				}
				defaultFound = true
			}
		}
	}

}
