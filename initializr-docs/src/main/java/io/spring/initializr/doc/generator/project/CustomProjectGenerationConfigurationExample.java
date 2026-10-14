/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration example of a custom {@link ProjectGenerationController}.
 *
 * @author Stephane Nicoll
 */
@Configuration
public class CustomProjectGenerationConfigurationExample {

	// tag::code[]
	@Bean
	public CustomProjectGenerationController projectGenerationController(InitializrMetadataProvider metadataProvider,
			ApplicationContext applicationContext) {
		ProjectGenerationInvoker<CustomProjectRequest> projectGenerationInvoker = new ProjectGenerationInvoker<>(
				applicationContext, new CustomProjectRequestToDescriptionConverter());
		return new CustomProjectGenerationController(metadataProvider, projectGenerationInvoker);
	}
	// end::code[]

	static class CustomProjectRequestToDescriptionConverter
			implements ProjectRequestToDescriptionConverter<CustomProjectRequest> {

		@Override
		public ProjectDescription convert(CustomProjectRequest request, InitializrMetadata metadata) {
			return new MutableProjectDescription();
		}

	}

}
