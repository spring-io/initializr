/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.configuration;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for application-related contributions to a generated project.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
public class ApplicationConfigurationProjectGenerationConfiguration {

	@Bean
	WebFoldersContributor webFoldersContributor(Build build, InitializrMetadata metadata,
			ProjectDescription projectDescription) {
		return new WebFoldersContributor(build, metadata, projectDescription);
	}

}
