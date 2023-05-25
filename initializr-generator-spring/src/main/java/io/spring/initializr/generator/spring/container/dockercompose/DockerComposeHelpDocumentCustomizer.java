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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.spring.initializr.generator.spring.container.dockercompose.Markdown.MarkdownTable;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.documentation.HelpDocumentCustomizer;

/**
 * Provide additional information in the {@link HelpDocument} if the
 * {@link DockerComposeFile} isn't empty.
 *
 * @author Moritz Halbritter
 */
class DockerComposeHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final DockerComposeFile composeFile;

	DockerComposeHelpDocumentCustomizer(DockerComposeFile composeFile) {
		this.composeFile = composeFile;
	}

	@Override
	public void customize(HelpDocument document) {
		Collection<DockerComposeService> services = this.composeFile.getServices();
		Map<String, Object> model = new HashMap<>();
		if (services.isEmpty()) {
			model.put("serviceTable", null);
			document.getWarnings()
				.addItem(
						"No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file.");
		}
		else {
			MarkdownTable serviceTable = Markdown.table("Service name", "Image", "Tag", "Website");
			for (DockerComposeService service : services) {
				serviceTable.addRow(service.getName(), Markdown.code(service.getImage()),
						Markdown.code(service.getImageTag()), Markdown.link("Website", service.getImageWebsite()));
			}
			model.put("serviceTable", serviceTable.toMarkdown());
		}
		document.addSection("documentation/docker-compose", model);
	}

}
