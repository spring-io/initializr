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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HelpDocumentProjectContributor}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentProjectContributorTests {

	private Path directory;

	private MustacheTemplateRenderer templateRenderer;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.directory = directory;
		this.templateRenderer = new MustacheTemplateRenderer("classpath:/templates");
	}

	@Test
	void helpDocumentEmptyDoesNotCreateFile() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		assertThat(document.isEmpty()).isTrue();
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		new HelpDocumentProjectContributor(document).contribute(projectDir);
		Path helpDocument = projectDir.resolve("HELP.md");
		assertThat(helpDocument).doesNotExist();
	}

	@Test
	void helpDocumentWithLinksToGuide() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addGuideLink("https://test.example.com", "test")
				.addGuideLink("https://test2.example.com", "test2");
		List<String> lines = generateDocument(document);
		assertThat(lines).containsExactly("# Getting Started", "", "### Guides",
				"The following guides illustrate how to use some features concretely:",
				"", "* [test](https://test.example.com)",
				"* [test2](https://test2.example.com)");
	}

	@Test
	void helpDocumentWithLinksToReferenceDoc() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addReferenceDocLink("https://test.example.com", "doc")
				.addReferenceDocLink("https://test2.example.com", "doc2");
		List<String> lines = generateDocument(document);
		assertThat(lines).containsExactly("# Getting Started", "",
				"### Reference Documentation",
				"For further reference, please consider the following sections:", "",
				"* [doc](https://test.example.com)",
				"* [doc2](https://test2.example.com)");
	}

	@Test
	void helpDocumentWithLinksToOtherLinks() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addAdditionalLink("https://test.example.com",
				"Something");
		List<String> lines = generateDocument(document);
		assertThat(lines).containsExactly("# Getting Started", "", "### Additional Links",
				"These additional references should also help you:", "",
				"* [Something](https://test.example.com)");
	}

	@Test
	void helpDocumentWithSimpleSection() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.addSection((writer) -> writer
				.println(String.format("# My test section%n%n    * Test")));
		List<String> lines = generateDocument(document);
		assertThat(lines).containsExactly("# My test section", "", "    * Test");
	}

	@Test
	void helpDocumentWithLinksAndSimpleSection() throws IOException {
		HelpDocument document = new HelpDocument(this.templateRenderer);
		document.gettingStarted().addGuideLink("https://test.example.com", "test")
				.addSection((writer) -> writer
						.println(String.format("# My test section%n%n    * Test")));
		List<String> lines = generateDocument(document);
		assertThat(lines).containsExactly("# Getting Started", "", "### Guides",
				"The following guides illustrate how to use some features concretely:",
				"", "* [test](https://test.example.com)", "", "# My test section", "",
				"    * Test");
	}

	private List<String> generateDocument(HelpDocument document) throws IOException {
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		new HelpDocumentProjectContributor(document).contribute(projectDir);
		return new ProjectStructure(projectDir).readAllLines("HELP.md");
	}

}
