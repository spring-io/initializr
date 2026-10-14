/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IndentingWriter}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class IndentingWriterTests {

	private final StringWriter stringWriter = new StringWriter();

	private final IndentingWriter indentingWriter = new IndentingWriter(this.stringWriter,
			new SimpleIndentStrategy("\t"));

	@Test
	void linesAreNotIndentedByDefault() {
		this.indentingWriter.println("a");
		this.indentingWriter.println("b");
		this.indentingWriter.println("c");
		assertThat(content()).contains("""
				a
				b
				c""");
	}

	@Test
	void linesCanBeIndented() {
		this.indentingWriter.println("a");
		this.indentingWriter.indented(() -> this.indentingWriter.println("b"));
		this.indentingWriter.println("c");
		assertThat(content()).contains("""
				a
					b
				c""");
	}

	@Test
	void blankLinesAreNotIndented() {
		this.indentingWriter.println("a");
		this.indentingWriter.indented(() -> {
			this.indentingWriter.println("b");
			this.indentingWriter.println();
		});
		this.indentingWriter.println("c");
		assertThat(content()).contains("""
				a
					b

				c""");
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
		assertThat(content()).contains("""
				a
					bbb
				c""");
	}

	@Test
	void defaultIndentStrategyIsUsed() throws IOException {
		try (IndentingWriter customIndentingWriter = new IndentingWriter(this.stringWriter)) {
			customIndentingWriter.println("a");
			customIndentingWriter.indented(() -> {
				customIndentingWriter.println("b");
				customIndentingWriter.indented(() -> {
					customIndentingWriter.print("c");
					customIndentingWriter.println("e");
				});
			});
		}
		assertThat(content().lines()).contains("a", "    b", "        ce");
	}

	private String content() {
		return this.stringWriter.toString().replace("\r\n", "\n");
	}

}
