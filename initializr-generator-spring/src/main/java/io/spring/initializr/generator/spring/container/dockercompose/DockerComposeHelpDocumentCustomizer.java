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

import java.util.Comparator;
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
		if (this.composeFile.isEmpty()) {
			return;
		}
		MarkdownTable serviceTable = Markdown.table("Service name", "Image", "Tag", "Website");
		this.composeFile.getServices()
			.stream()
			.sorted(Comparator.comparing(DockerComposeService::getName))
			.forEach((service) -> serviceTable.addRow(service.getName(), Markdown.code(service.getImage()),
					Markdown.code(service.getImageTag()), Markdown.link("Website", service.getImageWebsite())));
		Map<String, Object> model = new HashMap<>();
		model.put("serviceTable", serviceTable.toMarkdown());
		document.addSection("documentation/docker-compose", model);
	}

}
