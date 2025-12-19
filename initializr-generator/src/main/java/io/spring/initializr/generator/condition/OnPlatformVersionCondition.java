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
