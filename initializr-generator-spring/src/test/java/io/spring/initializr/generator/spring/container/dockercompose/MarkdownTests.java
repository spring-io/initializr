/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.container.dockercompose;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Markdown}.
 *
 * @author Moritz Halbritter
 */
class MarkdownTests {

	@Test
	void shouldFormatCode() {
		String code = Markdown.code("c = a + b");
		assertThat(code).isEqualTo("`c = a + b`");
	}

	@Test
	void shouldFormatLink() {
		String link = Markdown.link("Spring Website", "https://spring.io/");
		assertThat(link).isEqualTo("[Spring Website](https://spring.io/)");
	}

}
