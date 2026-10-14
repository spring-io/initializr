/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnBuildSystem}.
 *
 * @author Andy Wilkinson
 */
class OnBuildSystemCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		MultiValueMap<String, @Nullable Object> attributes = metadata
			.getAllAnnotationAttributes(ConditionalOnBuildSystem.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		String value = (String) attributes.getFirst("value");
		String dialect = (String) attributes.getFirst("dialect");
		BuildSystem buildSystem = description.getBuildSystem();
		if (buildSystem != null && buildSystem.id().equals(value)) {
			if (StringUtils.hasText(dialect)) {
				return dialect.equals(buildSystem.dialect());
			}
			return true;
		}
		return false;
	}

}
