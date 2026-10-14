/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.project.ProjectDescription;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

/**
 * Base class for project generation {@link Condition Conditions} that rely on the state
 * of the {@link ProjectDescription}.
 *
 * @author Andy Wilkinson
 */
public abstract class ProjectGenerationCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Assert.state(context.getBeanFactory() != null, "'context.getBeanFactory()' must not be null");
		ProjectDescription description = context.getBeanFactory().getBean(ProjectDescription.class);
		return matches(description, context, metadata);
	}

	/**
	 * Determine if the condition matches.
	 * @param description the project description
	 * @param context the condition context
	 * @param metadata the metadata of the annotated element
	 * @return {@code true} if the condition matches
	 */
	protected abstract boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata);

}
