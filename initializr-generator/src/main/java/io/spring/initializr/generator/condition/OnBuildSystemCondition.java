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

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.project.ResolvedProjectDescription;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnBuildSystem}.
 *
 * @author Andy Wilkinson
 */
class OnBuildSystemCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ResolvedProjectDescription projectDescription,
			ConditionContext context, AnnotatedTypeMetadata metadata) {
		String buildSystemId = (String) metadata
				.getAllAnnotationAttributes(ConditionalOnBuildSystem.class.getName())
				.getFirst("value");
		BuildSystem buildSystem = BuildSystem.forId(buildSystemId);
		return projectDescription.getBuildSystem().id().equals(buildSystem.id());
	}

}
