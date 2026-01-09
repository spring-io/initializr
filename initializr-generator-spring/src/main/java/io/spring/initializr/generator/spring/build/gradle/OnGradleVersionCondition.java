/*
 * Copyright 2012 - present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.spring.build.gradle;

import java.util.Arrays;
import java.util.Map;

import io.spring.initializr.generator.condition.ProjectGenerationCondition;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

/**
 * {@link ProjectGenerationCondition} implementation for
 * {@link ConditionalOnGradleVersion}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class OnGradleVersionCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		String gradleGeneration = determineGradleGeneration(description.getPlatformVersion());
		if (gradleGeneration == null) {
			return false;
		}
		Map<String, @Nullable Object> attributes = metadata
			.getAnnotationAttributes(ConditionalOnGradleVersion.class.getName());
		Assert.state(attributes != null, "'attributes' must not be null");
		String[] values = (String[]) attributes.get("value");
		Assert.state(values != null, "'values' must not be null");
		return Arrays.asList(values).contains(gradleGeneration);
	}

	private @Nullable String determineGradleGeneration(@Nullable Version platformVersion) {
		if (platformVersion == null) {
			return null;
		}
		// Use Gradle 9 for Spring Boot 4.x, Gradle 8 for Spring Boot 3.x
		if (platformVersion.getMajor() != null && platformVersion.getMajor() >= 4) {
			return "9";
		}
		return "8";
	}

}
