/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionChange;
import io.spring.initializr.generator.project.ProjectDescriptionChanges;

/**
 * A {@link HelpDocumentCustomizer} that adds a warning for every change that has been
 * recorded in the {@link ProjectDescriptionChanges} of the description.
 *
 * @author Moritz Halbritter
 */
class ProjectDescriptionChangesHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final ProjectDescription description;

	ProjectDescriptionChangesHelpDocumentCustomizer(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void customize(HelpDocument document) {
		// Only a mutable description can be customized, so any other description cannot
		// have recorded a change
		if (!(this.description instanceof MutableProjectDescription mutableDescription)) {
			return;
		}
		for (ProjectDescriptionChange change : mutableDescription.getChanges().getAll()) {
			document.getWarnings().addItem(change.getReason());
		}
	}

}
