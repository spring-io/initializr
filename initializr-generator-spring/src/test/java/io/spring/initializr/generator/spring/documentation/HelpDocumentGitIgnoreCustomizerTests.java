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
