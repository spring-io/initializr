/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link HelpDocument}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentTests {

	private final MustacheTemplateRenderer templateRenderer = new MustacheTemplateRenderer("classpath:/templates");

	@Test
	void renderEmptyDocumentDoesNotCallWriter() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		PrintWriter out = mock(PrintWriter.class);
		document.write(out);
		verifyNoInteractions(out);
	}

	@Test
	void renderSingleSection() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.addSection((writer) -> writer.println("# Test"));
		String out = write(document);
		assertThat(out).contains("# Test", "");
	}

	@Test
	void renderWarnings() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.getWarnings().addItem("Invalid groupId '#invalid#'");
		document.getWarnings().addItem("Invalid package name '#invalid#'");
		String out = write(document);
		assertThat(out).contains("# Read Me First", "The following was discovered as part of building this project:",
				"", "* Invalid groupId '#invalid#'", "* Invalid package name '#invalid#'");
	}

	@Test
	void renderLinks() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addReferenceDocLink("https://example.com/doc", "Doc");
		document.gettingStarted().addGuideLink("https://example.com/guide-1", "Guide 1");
		document.gettingStarted().addGuideLink("https://example.com/guide-2", "Guide 2");
		String out = write(document);
		assertThat(out).contains("# Getting Started", "", "### Reference Documentation",
				"For further reference, please consider the following sections:", "",
				"* [Doc](https://example.com/doc)", "", "### Guides",
				"The following guides illustrate how to use some features concretely:", "",
				"* [Guide 1](https://example.com/guide-1)", "* [Guide 2](https://example.com/guide-2)", "");
	}

	@Test
	void renderOnlyAdditionalLink() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addAdditionalLink("https://example.com/app", "Test App");
		String out = write(document);
		assertThat(out).contains("# Getting Started", "", "### Additional Links",
				"These additional references should also help you:", "", "* [Test App](https://example.com/app)", "");
	}

	private String write(HelpDocument document) {
		try {
			StringWriter out = new StringWriter();
			document.write(new PrintWriter(out));
			return out.toString();
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
