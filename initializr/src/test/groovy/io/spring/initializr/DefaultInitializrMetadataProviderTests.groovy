/*
 * Copyright 2012-2014 the original author or authors.
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

import io.spring.initializr.support.InitializrMetadataBuilder
import org.junit.Test

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
class DefaultInitializrMetadataProviderTests {

	@Test
	void bootVersionsAreReplaced() {
		def metadata = new InitializrMetadataBuilder()
				.addBootVersion('0.0.9.RELEASE', true).addBootVersion('0.0.8.RELEASE', false).validateAndGet()
		assertEquals '0.0.9.RELEASE', metadata.defaults.bootVersion
		def provider = new DefaultInitializrMetadataProvider(metadata)

		def updatedMetadata = provider.get()
		assertNotNull updatedMetadata.bootVersions
		assertFalse 'Boot versions must be set', updatedMetadata.bootVersions.isEmpty()

		def defaultVersion = null
		updatedMetadata.bootVersions.each {
			assertFalse '0.0.9.RELEASE should have been removed', '0.0.9.RELEASE'.equals(it.id)
			assertFalse '0.0.8.RELEASE should have been removed', '0.0.8.RELEASE'.equals(it.id)
			if (it.default) {
				defaultVersion = it.id
			}
		}
		assertNotNull 'A default boot version must be set', defaultVersion
		assertEquals 'Default boot version not updated properly', defaultVersion, updatedMetadata.defaults.bootVersion
	}

}
