/*
 * Copyright 2012-2024 the original author or authors.
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
