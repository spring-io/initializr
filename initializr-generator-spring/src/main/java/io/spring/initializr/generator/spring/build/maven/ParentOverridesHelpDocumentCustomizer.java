/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.maven;

import java.util.Collections;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSettings;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.documentation.HelpDocumentCustomizer;

/**
 * {@link HelpDocumentCustomizer} to add a section about the parent overrides in place.
 *
 * @author Moritz Halbritter
 */
class ParentOverridesHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final MavenBuildSettings buildSettings;

	ParentOverridesHelpDocumentCustomizer(MavenBuildSettings buildSettings) {
		this.buildSettings = buildSettings;
	}

	@Override
	public void customize(HelpDocument document) {
		if (this.buildSettings.isAddOverrideIfEmpty()) {
			document.addSection("documentation/parent-overrides", Collections.emptyMap());
		}
	}

}
