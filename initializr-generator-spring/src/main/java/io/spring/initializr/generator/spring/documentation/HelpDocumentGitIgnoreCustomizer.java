/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.spring.scm.git.GitIgnore;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;

/**
 * A {@link GitIgnoreCustomizer} that adds {@code HELP.md} to the ignore list when
 * applicable.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentGitIgnoreCustomizer implements GitIgnoreCustomizer {

	private final HelpDocument document;

	HelpDocumentGitIgnoreCustomizer(HelpDocument document) {
		this.document = document;
	}

	@Override
	public void customize(GitIgnore gitIgnore) {
		if (!this.document.isEmpty()) {
			gitIgnore.getGeneral().add("HELP.md");
		}
	}

}
