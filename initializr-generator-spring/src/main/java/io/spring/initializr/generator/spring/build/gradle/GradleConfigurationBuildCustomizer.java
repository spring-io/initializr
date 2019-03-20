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

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

import org.springframework.core.Ordered;

/**
 * Gradle {@link BuildCustomizer} that creates the necessary {@code configuration}.
 *
 * @author Stephane Nicoll
 */
public class GradleConfigurationBuildCustomizer implements BuildCustomizer<GradleBuild> {

	@Override
	public void customize(GradleBuild build) {
		boolean providedRuntimeUsed = build.dependencies().items()
				.anyMatch((dependency) -> DependencyScope.PROVIDED_RUNTIME
						.equals(dependency.getScope()));
		boolean war = build.getPlugins().stream()
				.anyMatch((plugin) -> plugin.getId().equals("war"));
		if (providedRuntimeUsed && !war) {
			build.addConfiguration("providedRuntime");
		}
		boolean annotationProcessorUsed = build.dependencies().items()
				.anyMatch((dependency) -> dependency
						.getScope() == DependencyScope.ANNOTATION_PROCESSOR);
		if (annotationProcessorUsed) {
			build.customizeConfiguration("compileOnly",
					(configuration) -> configuration.extendsFrom("annotationProcessor"));
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
