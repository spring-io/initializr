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

package io.spring.initializr.generator.spring.documentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests for {@link HelpDocument}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentTests {

	private final MustacheTemplateRenderer templateRenderer = new MustacheTemplateRenderer(
			"classpath:/templates");

	@Test
	void renderEmptyDocumentDoesNotCallWriter() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		PrintWriter out = mock(PrintWriter.class);
		document.write(out);
		verifyZeroInteractions(out);
	}

	@Test
	void renderSingleSection() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.addSection((writer) -> writer.println("# Test"));
		String out = write(document);
		assertThat(out).contains("# Test", "");
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
				"The following guides illustrate how to use some features concretely:",
				"", "* [Guide 1](https://example.com/guide-1)",
				"* [Guide 2](https://example.com/guide-2)", "");
	}

	@Test
	void renderOnlyAdditionalLink() {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addAdditionalLink("https://example.com/app",
				"Test App");
		String out = write(document);
		assertThat(out).contains("# Getting Started", "", "### Additional Links",
				"These additional references should also help you:", "",
				"* [Test App](https://example.com/app)", "");
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
