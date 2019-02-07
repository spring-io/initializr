/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.condition.ProjectGenerationCondition;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link ProjectGenerationCondition} implementation for
 * {@link ConditionalOnGradleVersion}.
 *
 * @author Andy Wilkinson
 */
public class OnGradleVersionCondition extends ProjectGenerationCondition {

	private static final VersionRange GRADLE_3_BOOT_VERSION_RANGE = VersionParser.DEFAULT
			.parseRange("[1.5.0.M1,2.0.0.M1)");

	private static final VersionRange GRADLE_4_BOOT_VERSION_RANGE = VersionParser.DEFAULT
			.parseRange("2.0.0.M1");

	@Override
	protected boolean matches(ResolvedProjectDescription projectDescription,
			ConditionContext context, AnnotatedTypeMetadata metadata) {
		Version springBootVersion = projectDescription.getPlatformVersion();
		String gradleVersion;
		if (GRADLE_3_BOOT_VERSION_RANGE.match(springBootVersion)) {
			gradleVersion = "3";
		}
		else if (GRADLE_4_BOOT_VERSION_RANGE.match(springBootVersion)) {
			gradleVersion = "4";
		}
		else {
			return false;
		}
		String value = (String) metadata
				.getAnnotationAttributes(ConditionalOnGradleVersion.class.getName())
				.get("value");
		return gradleVersion.equals(value);
	}

}
