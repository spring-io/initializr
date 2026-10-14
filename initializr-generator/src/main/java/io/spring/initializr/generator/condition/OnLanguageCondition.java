/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.project.ProjectDescription;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnLanguage}.
 *
 * @author Andy Wilkinson
 */
class OnLanguageCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		if (description.getLanguage() == null) {
			return false;
		}
		MultiValueMap<String, @Nullable Object> attributes = metadata
			.getAllAnnotationAttributes(ConditionalOnLanguage.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		String value = (String) attributes.getFirst("value");
		Assert.state(value != null, "'value' must not be null");
		Language language = Language.forId(value, null);
		return description.getLanguage().id().equals(language.id());
	}

}
