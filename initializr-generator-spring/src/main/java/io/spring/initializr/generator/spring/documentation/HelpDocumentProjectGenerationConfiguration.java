/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Configuration for contributions specific to the help documentation of a project. If no
 * {@link MustacheTemplateRenderer} exists, a default one on {@code classpath:/templates}
 * is provided.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@Import(HelpDocumentProjectGenerationDefaultContributorsConfiguration.class)
public class HelpDocumentProjectGenerationConfiguration {

	@Bean
	public HelpDocument helpDocument(ObjectProvider<MustacheTemplateRenderer> templateRenderer,
			ObjectProvider<HelpDocumentCustomizer> helpDocumentCustomizers) {
		HelpDocument helpDocument = new HelpDocument(
				templateRenderer.getIfAvailable(() -> new MustacheTemplateRenderer("classpath:/templates")));
		helpDocumentCustomizers.orderedStream().forEach((customizer) -> customizer.customize(helpDocument));
		return helpDocument;
	}

	@Bean
	public HelpDocumentProjectContributor helpDocumentProjectContributor(HelpDocument helpDocument) {
		return new HelpDocumentProjectContributor(helpDocument);
	}

}
