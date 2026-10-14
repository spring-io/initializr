/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.container.docker.compose;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.spring.initializr.generator.container.docker.compose.ComposeFile;
import io.spring.initializr.generator.container.docker.compose.ComposeService;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.documentation.HelpDocumentCustomizer;

/**
 * A {@link HelpDocumentCustomizer} that provide additional information about the
 * {@link ComposeService compose services} that are defined for the project.
 *
 * @author Moritz Halbritter
 */
public class ComposeHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final ComposeFile composeFile;

	public ComposeHelpDocumentCustomizer(ComposeFile composeFile) {
		this.composeFile = composeFile;
	}

	@Override
	public void customize(HelpDocument document) {
		Map<String, Object> model = new HashMap<>();
		if (this.composeFile.services().isEmpty()) {
			document.getWarnings()
				.addItem(
						"No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file.");
		}
		else {
			model.put("services",
					this.composeFile.services()
						.values()
						.sorted(Comparator.comparing(ComposeService::getName))
						.toList());
		}
		document.addSection("documentation/docker-compose", model);
	}

}
