/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.project.ProjectDescription;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnConfigurationFileFormat}.
 *
 * @author Sijun Yang
 */
class OnConfigurationFileFormatCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		if (description.getConfigurationFileFormat() == null) {
			return false;
		}
		MultiValueMap<String, @Nullable Object> attributes = metadata
			.getAllAnnotationAttributes(ConditionalOnConfigurationFileFormat.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		String value = (String) attributes.getFirst("value");
		Assert.state(value != null, "'value' must not be null");
		ConfigurationFileFormat configurationFileFormat = ConfigurationFileFormat.forId(value);
		return description.getConfigurationFileFormat().id().equals(configurationFileFormat.id());
	}

}
