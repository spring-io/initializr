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

import io.spring.initializr.generator.spring.container.dockercompose.Markdown.MarkdownTable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link MarkdownTable}.
 *
 * @author Moritz Halbritter
 */
class MarkdownTableTests {

	@Test
	void shouldFormatCorrectly() {
		MarkdownTable table = new MarkdownTable("a", "b1", "c22", "d333");
		table.addRow("0", "1", "2", "3");
		table.addRow("4", "5", "6", "7");
		String markdown = table.toMarkdown();
		assertThat(markdown).isEqualToIgnoringNewLines("""
				| a | b1 | c22 | d333 |
				| - | -- | --- | ---- |
				| 0 | 1  | 2   | 3    |
				| 4 | 5  | 6   | 7    |
				""");
	}

	@Test
	void rowIsBiggerThanHeading() {
		MarkdownTable table = new MarkdownTable("a", "b", "c", "d");
		table.addRow("0.0", "1.1", "2.2", "3.3");
		table.addRow("4.4", "5.5", "6.6", "7.7");
		String markdown = table.toMarkdown();
		assertThat(markdown).isEqualToIgnoringNewLines("""
				| a   | b   | c   | d   |
				| --- | --- | --- | --- |
				| 0.0 | 1.1 | 2.2 | 3.3 |
				| 4.4 | 5.5 | 6.6 | 7.7 |
				""");
	}

	@Test
	void throwsIfCellsDifferFromHeader() {
		MarkdownTable table = new MarkdownTable("a", "b", "c", "d");
		assertThatThrownBy(() -> table.addRow("1")).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Expected 4 cells, got 1");
	}

}
