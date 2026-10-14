/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import io.spring.initializr.generator.condition.ConditionalOnConfigurationFileFormat;
import io.spring.initializr.generator.configuration.format.properties.PropertiesFormat;
import io.spring.initializr.generator.configuration.format.yaml.YamlFormat;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for application properties related contributions to a generated project.
 *
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
class ApplicationPropertiesProjectGenerationConfiguration {

	@Bean
	ApplicationProperties applicationProperties(ObjectProvider<ApplicationPropertiesCustomizer> customizers) {
		ApplicationProperties properties = new ApplicationProperties();
		customizers.orderedStream().forEach((customizer) -> customizer.customize(properties));
		return properties;
	}

	@Bean
	@ConditionalOnConfigurationFileFormat(PropertiesFormat.ID)
	ApplicationPropertiesContributor applicationPropertiesContributor(ApplicationProperties applicationProperties,
			ProjectDescription description) {
		return new ApplicationPropertiesContributor(applicationProperties, description);
	}

	@Bean
	@ConditionalOnConfigurationFileFormat(YamlFormat.ID)
	ApplicationYamlPropertiesContributor applicationYamlPropertiesContributor(
			ApplicationProperties applicationProperties, ProjectDescription description) {
		return new ApplicationYamlPropertiesContributor(applicationProperties, description);
	}

}
