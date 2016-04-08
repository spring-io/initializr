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

package io.spring.initializr.test.generator

import java.nio.charset.Charset

import org.springframework.core.io.Resource
import org.springframework.util.StreamUtils

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Source code assertions.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class SourceCodeAssert {

	private final String name
	private final String content

	SourceCodeAssert(String name, String content) {
		this.name = name
		this.content = content
	}

	SourceCodeAssert equalsTo(Resource expected) {
		def stream = expected.inputStream
		try {
			String expectedContent = StreamUtils.copyToString(stream, Charset.forName('UTF-8'))
			assertEquals "Unexpected content for $name", expectedContent, content
		} finally {
			stream.close()
		}
		this
	}

	SourceCodeAssert hasImports(String... classNames) {
		for (String className : classNames) {
			contains("import $className")
		}
		this
	}

	SourceCodeAssert doesNotHaveImports(String... classNames) {
		for (String className : classNames) {
			doesNotContain("import $className")
		}
		this
	}

	SourceCodeAssert contains(String... expressions) {
		for (String expression : expressions) {
			assertTrue "$expression has not been found in source code '$name'", content.contains(expression)
		}
		this
	}

	SourceCodeAssert doesNotContain(String... expressions) {
		for (String expression : expressions) {
			assertFalse "$expression should not have been found in source code '$name'", content.contains(expression)
		}
		this
	}

}
