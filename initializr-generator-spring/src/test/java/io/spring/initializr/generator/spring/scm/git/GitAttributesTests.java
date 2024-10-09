/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.spring.scm.git;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GitAttributes}.
 *
 * @author Moritz Halbritter
 */
class GitAttributesTests {

	@Test
	void shouldWriteGitAttributes() {
		GitAttributes attributes = new GitAttributes();
		attributes.add("/gradlew", "text", "eof=lf");
		attributes.add("*.bat", "text", "eol=crlf");
		attributes.add("*.jar", "binary");
		String written = writeToString(attributes);
		assertThat(written).isEqualToNormalizingNewlines("""
				/gradlew text eof=lf
				*.bat text eol=crlf
				*.jar binary
				""");
	}

	private String writeToString(GitAttributes attributes) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
			attributes.write(printWriter);
		}
		return stringWriter.toString();
	}

}
