/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.util.Arrays;
import java.util.Map;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

/**
 * {@link ProjectGenerationCondition} implementation for
 * {@link ConditionalOnPlatformVersion}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class OnPlatformVersionCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		Version platformVersion = description.getPlatformVersion();
		if (platformVersion == null) {
			return false;
		}
		Map<String, @Nullable Object> attributes = metadata
			.getAnnotationAttributes(ConditionalOnPlatformVersion.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		Object value = attributes.get("value");
		Assert.state(value != null, "'value' must not be null");
		return Arrays.stream((String[]) value)
			.anyMatch((range) -> VersionParser.DEFAULT.parseRange(range).match(platformVersion));

	}

}
