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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.io.text.MustacheSection;
import io.spring.initializr.generator.io.text.Section;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerComposeHelpDocumentCustomizer}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeHelpDocumentCustomizerTests {

	private DockerComposeHelpDocumentCustomizer customizer;

	private DockerComposeFile dockerComposeFile;

	@BeforeEach
	void setUp() {
		this.dockerComposeFile = new DockerComposeFile();
		this.customizer = new DockerComposeHelpDocumentCustomizer(this.dockerComposeFile);
	}

	@Test
	void addsDockerComposeSection() throws IOException {
		this.dockerComposeFile.addService(DockerComposeServiceFixtures.service());
		HelpDocument helpDocument = helpDocument();
		this.customizer.customize(helpDocument);
		assertThat(helpDocument.getSections()).hasSize(1);
		Section section = helpDocument.getSections().get(0);
		assertThat(section).isInstanceOf(MustacheSection.class);
		StringWriter stringWriter = new StringWriter();
		helpDocument.write(new PrintWriter(stringWriter));
		assertThat(stringWriter.toString()).isEqualToIgnoringNewLines("""
				### Docker Compose support

				This project contains a Docker Compose file named `compose.yaml`.

				In this file, the following services have been defined:

				| Service name | Image     | Tag           | Website                           |
				| ------------ | --------- | ------------- | --------------------------------- |
				| service-1    | `image-1` | `image-tag-1` | [Website](https://service-1.org/) |


				Please review the tags of the used images and set them to the same as you're running in production.""");
	}

	@Test
	void addsWarningIfNoServicesAreDefined() throws IOException {
		HelpDocument helpDocument = helpDocument();
		this.customizer.customize(helpDocument);
		assertThat(helpDocument.getWarnings().getItems()).containsExactly(
				"No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file");
		StringWriter stringWriter = new StringWriter();
		helpDocument.write(new PrintWriter(stringWriter));
		assertThat(stringWriter.toString()).isEqualToIgnoringNewLines(
				"""
						# Read Me First
						The following was discovered as part of building this project:

						* No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file.

						### Docker Compose support

						This project contains a Docker Compose file named `compose.yaml`.

						However, no services were found. As of now, the application won't start!

						Please make sure to add at least one service in the `compose.yaml` file.""");
	}

	private static HelpDocument helpDocument() {
		return new HelpDocument(new MustacheTemplateRenderer("/templates"));
	}

}
