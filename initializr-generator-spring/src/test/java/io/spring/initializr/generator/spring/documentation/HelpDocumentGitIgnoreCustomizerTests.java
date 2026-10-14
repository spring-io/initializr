/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.spring.scm.git.GitIgnore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link HelpDocumentGitIgnoreCustomizer}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentGitIgnoreCustomizerTests {

	private final GitIgnore gitIgnore = new GitIgnore();

	@Test
	void gitIgnoreIsUpdatedWithNonEmptyHelpDocument() {
		HelpDocument document = new HelpDocument(mock(MustacheTemplateRenderer.class));
		document.addSection((writer) -> writer.println("test"));
		new HelpDocumentGitIgnoreCustomizer(document).customize(this.gitIgnore);
		assertThat(this.gitIgnore.getGeneral().getItems()).containsOnly("HELP.md");
	}

	@Test
	void gitIgnoreIsNotUpdatedWithEmptyHelpDocument() {
		HelpDocument document = new HelpDocument(mock(MustacheTemplateRenderer.class));
		new HelpDocumentGitIgnoreCustomizer(document).customize(this.gitIgnore);
		assertThat(this.gitIgnore.getGeneral().getItems()).isEmpty();
	}

}
