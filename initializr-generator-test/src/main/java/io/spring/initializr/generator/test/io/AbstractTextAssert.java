/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.test.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ListAssert;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * Base class for text assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public abstract class AbstractTextAssert<SELF extends AbstractStringAssert<SELF>> extends AbstractStringAssert<SELF> {

	protected AbstractTextAssert(String actual, Class<?> selfType) {
		super(actual, selfType);
	}

	protected AbstractTextAssert(Path textFile, Class<?> selfType) {
		this(TextTestUtils.readContent(textFile), selfType);
		this.info.description("Content at " + textFile);
	}

	/**
	 * Assert this text has the same content as the content defined by the specified
	 * {@link Resource}. Differences in newlines are ignored
	 * @param expected a resource with the expected content
	 * @return {@code this} assertion object
	 * @see #isEqualToIgnoringNewLines(CharSequence)
	 */
	public SELF hasSameContentAs(Resource expected) {
		if (!expected.isReadable()) {
			failWithMessage("Expected resource does not exist: " + expected);
		}
		try (InputStream in = expected.getInputStream()) {
			String expectedContent = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			isEqualToIgnoringNewLines(expectedContent);
		}
		catch (IOException ex) {
			failWithMessage("Cannot read expected content " + expected);
		}
		return this.myself;
	}

	/**
	 * Assert this text contains exactly the specified lines.
	 * @param lines the lines that constitute the content of this text
	 * @return {@code this} assertion object
	 */
	public SELF containsExactly(String... lines) {
		lines().containsExactly(lines);
		return this.myself;
	}

	/**
	 * Return an {@link ListAssert assert} for the lines that constitute this text, to
	 * allow chaining of lines-specific assertions from this call.
	 * @return a {@link ListAssert} for the lines that constitutes this text
	 */
	public ListAssert<String> lines() {
		return new ListAssert<>(TextTestUtils.readAllLines(this.actual));
	}

}
