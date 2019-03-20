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

package io.spring.initializr.generator.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import io.spring.initializr.generator.test.io.TextTestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IndentingWriter}.
 *
 * @author Andy Wilkinson
 */
class IndentingWriterTests {

	private final StringWriter stringWriter = new StringWriter();

	private final IndentingWriter indentingWriter = new IndentingWriter(
			this.stringWriter);

	@Test
	void linesAreNotIndentedByDefault() {
		this.indentingWriter.println("a");
		this.indentingWriter.println("b");
		this.indentingWriter.println("c");
		assertThat(readLines()).containsSequence("a", "b", "c");
	}

	@Test
	void linesCanBeIndented() {
		this.indentingWriter.println("a");
		this.indentingWriter.indented(() -> this.indentingWriter.println("b"));
		this.indentingWriter.println("c");
		assertThat(readLines()).containsSequence("a", "    b", "c");
	}

	@Test
	void blankLinesAreNotIndented() {
		this.indentingWriter.println("a");
		this.indentingWriter.indented(() -> {
			this.indentingWriter.println("b");
			this.indentingWriter.println();
		});
		this.indentingWriter.println("c");
		assertThat(readLines()).containsSequence("a", "    b", "", "c");
	}

	@Test
	void useOfPrintDoesNotAddIndent() {
		this.indentingWriter.println("a");
		this.indentingWriter.indented(() -> {
			this.indentingWriter.print("b");
			this.indentingWriter.print("b");
			this.indentingWriter.println("b");
		});
		this.indentingWriter.println("c");
		assertThat(readLines()).containsSequence("a", "    bbb", "c");
	}

	@Test
	void customIndentStrategyIsUsed() throws IOException {
		try (IndentingWriter customIndentingWriter = new IndentingWriter(
				this.stringWriter, new SimpleIndentStrategy("\t"))) {
			customIndentingWriter.println("a");
			customIndentingWriter.indented(() -> {
				customIndentingWriter.println("b");
				customIndentingWriter.indented(() -> {
					customIndentingWriter.print("c");
					customIndentingWriter.println("e");
				});
			});
		}
		assertThat(readLines()).containsSequence("a", "\tb", "\t\tce");
	}

	private List<String> readLines() {
		return TextTestUtils.readAllLines(this.stringWriter.toString());
	}

}
