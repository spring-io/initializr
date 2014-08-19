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

import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataTests {

	@Test
	void getDefaultNoDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', false)
		assertEquals 'three', InitializrMetadata.getDefault(elements, 'three')
	}

	@Test
	void getDefaultWithDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', true)
		assertEquals 'two', InitializrMetadata.getDefault(elements, 'three')
	}

	private static InitializrMetadata.JavaVersion createJavaVersion(String version, boolean selected) {
		InitializrMetadata.JavaVersion javaVersion = new InitializrMetadata.JavaVersion()
		javaVersion.id = version
		javaVersion.default = selected
		javaVersion
	}
}
