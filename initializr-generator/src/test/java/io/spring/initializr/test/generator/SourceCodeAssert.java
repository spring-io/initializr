/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.test.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Source code assertions.
 *
 * @author Stephane Nicoll
 */
public class SourceCodeAssert {

	private final String name;

	private final String content;

	public SourceCodeAssert(String name, String content) {
		this.name = name;
		this.content = content.replaceAll("\r\n", "\n");
	}

	public SourceCodeAssert equalsTo(Resource expected) {
		try (InputStream stream = expected.getInputStream()) {
			String expectedContent = StreamUtils.copyToString(stream,
					Charset.forName("UTF-8"));
			assertThat(this.content).describedAs("Content for %s", this.name)
					.isEqualTo(expectedContent.replaceAll("\r\n", "\n"));
		}
		catch (IOException ex) {
			throw new IllegalStateException("Cannot read file", ex);
		}
		return this;
	}

	public SourceCodeAssert hasImports(String... classNames) {
		for (String className : classNames) {
			contains("import " + className);
		}
		return this;
	}

	public SourceCodeAssert doesNotHaveImports(String... classNames) {
		for (String className : classNames) {
			doesNotContain("import " + className);
		}
		return this;
	}

	public SourceCodeAssert contains(String... expressions) {
		assertThat(this.content).describedAs("Content for %s", this.name)
				.contains(expressions);
		return this;
	}

	public SourceCodeAssert doesNotContain(String... expressions) {
		assertThat(this.content).describedAs("Content for %s", this.name)
				.doesNotContain(expressions);
		return this;
	}

}
