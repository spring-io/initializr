/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ProjectDescriptionChangesHelpDocumentCustomizer}.
 *
 * @author Moritz Halbritter
 */
class ProjectDescriptionChangesHelpDocumentCustomizerTests {

	private final HelpDocument document = new HelpDocument(mock(MustacheTemplateRenderer.class));

	private final MutableProjectDescription description = new MutableProjectDescription();

	@Test
	void recordedReasonIsAddedAsWarning() {
		this.description.getChanges().add(ProjectDescriptionField.JVM_VERSION, "test reason");
		customize(this.description);
		assertThat(this.document.getWarnings().getItems()).containsExactly("test reason");
	}

	@Test
	void onlyTheLastRecordedReasonOfAFieldIsAddedAsWarning() {
		this.description.getChanges().add(ProjectDescriptionField.JVM_VERSION, "first reason");
		this.description.getChanges().add(ProjectDescriptionField.JVM_VERSION, "second reason");
		customize(this.description);
		assertThat(this.document.getWarnings().getItems()).containsExactly("second reason");
	}

	@Test
	void documentIsEmptyWhenNoReasonIsRecorded() {
		customize(this.description);
		assertThat(this.document.getWarnings().getItems()).isEmpty();
		assertThat(this.document.isEmpty()).isTrue();
	}

	@Test
	void documentIsEmptyWithNonMutableDescription() {
		customize(mock(ProjectDescription.class));
		assertThat(this.document.isEmpty()).isTrue();
	}

	private void customize(ProjectDescription description) {
		new ProjectDescriptionChangesHelpDocumentCustomizer(description).customize(this.document);
	}

}
