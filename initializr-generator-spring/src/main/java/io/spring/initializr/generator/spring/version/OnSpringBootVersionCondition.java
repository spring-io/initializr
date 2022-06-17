/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.spring.version;

import java.util.Arrays;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.util.SpringBootVersionCondition;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link SpringBootVersionCondition} implementation for
 * {@link ConditionalOnSpringBootVersion}.
 *
 * @author Filip Hrisafov
 */
class OnSpringBootVersionCondition extends SpringBootVersionCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context, AnnotatedTypeMetadata metadata,
			SpringBootProjectSettings settings) {
		Version springBootVersion = Version.safeParse(settings.getVersion());
		if (springBootVersion == null) {
			return false;
		}
		return Arrays
				.stream((String[]) metadata.getAnnotationAttributes(ConditionalOnSpringBootVersion.class.getName())
						.get("value"))
				.anyMatch((range) -> VersionParser.DEFAULT.parseRange(range).match(springBootVersion));

	}

}
