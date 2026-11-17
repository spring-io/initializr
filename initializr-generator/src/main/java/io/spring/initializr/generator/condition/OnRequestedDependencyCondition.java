/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.condition;

import java.util.Map;

import io.spring.initializr.generator.project.ProjectDescription;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

/**
 * {@link ProjectGenerationCondition} implementation for
 * {@link ConditionalOnRequestedDependency}.
 *
 * @author Andy Wilkinson
 */
class OnRequestedDependencyCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		Map<String, @Nullable Object> attributes = metadata
			.getAnnotationAttributes(ConditionalOnRequestedDependency.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		String value = (String) attributes.get("value");
		Assert.state(value != null, "'value' must not be null");
		return description.getRequestedDependencies().containsKey(value);
	}

}
