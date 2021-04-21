/*
 * Copyright 2012-2021 the original author or authors.
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

import io.spring.initializr.generator.condition.ProjectGenerationCondition;
import io.spring.initializr.generator.project.ProjectDescription;
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
 * @author Stephane Nicoll
 */
public class OnGradleVersionCondition extends ProjectGenerationCondition {

	private static final VersionRange GRADLE_6_VERSION_RANGE = VersionParser.DEFAULT
			.parseRange("[2.2.2.RELEASE,2.5.0-RC1)");

	private static final VersionRange GRADLE_7_VERSION_RANGE = VersionParser.DEFAULT.parseRange("2.5.0-RC1");

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		String gradleGeneration = determineGradleGeneration(description.getPlatformVersion());
		if (gradleGeneration == null) {
			return false;
		}
		String[] values = (String[]) metadata.getAnnotationAttributes(ConditionalOnGradleVersion.class.getName())
				.get("value");
		return Arrays.asList(values).contains(gradleGeneration);
	}

	private String determineGradleGeneration(Version platformVersion) {
		if (platformVersion == null) {
			return null;
		}
		else if (GRADLE_6_VERSION_RANGE.match(platformVersion)) {
			return "6";
		}
		else if (GRADLE_7_VERSION_RANGE.match(platformVersion)) {
			return "7";
		}
		else {
			return null;
		}
	}

}
