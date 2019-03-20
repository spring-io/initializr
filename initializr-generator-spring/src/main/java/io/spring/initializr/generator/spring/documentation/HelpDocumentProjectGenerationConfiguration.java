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
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Configuration for contributions specific to the help documentation of a project.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@Import(HelpDocumentProjectGenerationDefaultContributorsConfiguration.class)
public class HelpDocumentProjectGenerationConfiguration {

	@Bean
	public HelpDocument helpDocument(MustacheTemplateRenderer templateRenderer,
			ObjectProvider<HelpDocumentCustomizer> helpDocumentCustomizers) {
		HelpDocument helpDocument = new HelpDocument(templateRenderer);
		helpDocumentCustomizers.orderedStream()
				.forEach((customizer) -> customizer.customize(helpDocument));
		return helpDocument;
	}

	@Bean
	public HelpDocumentProjectContributor helpDocumentProjectContributor(
			HelpDocument helpDocument) {
		return new HelpDocumentProjectContributor(helpDocument);
	}

}
