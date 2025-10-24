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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that adds the default test starter if none is detected.
 *
 * @author Moritz Halbritter
 */
class DefaultTestStarterBuildCustomizer implements BuildCustomizer<Build> {

	private static final Dependency SPRING_BOOT_STARTER_TEST = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
		.scope(DependencyScope.TEST_COMPILE)
		.build();

	/**
	 * The id of the test starter to use if no dependency is defined.
	 */
	static final String STARTER_ID = "root_test_starter";

	@Override
	public void customize(Build build) {
		boolean hasTestStarter = build.dependencies().items().anyMatch(this::isValidTestStarter);
		if (!hasTestStarter) {
			build.dependencies().add(STARTER_ID, SPRING_BOOT_STARTER_TEST);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private boolean isValidTestStarter(Dependency dependency) {
		boolean isTestStarter = dependency.getGroupId().equals("org.springframework.boot")
				&& dependency.getArtifactId().startsWith("spring-boot-starter-")
				&& dependency.getArtifactId().endsWith("-test");
		return isTestStarter && DependencyScope.TEST_COMPILE.equals(dependency.getScope());
	}

}
