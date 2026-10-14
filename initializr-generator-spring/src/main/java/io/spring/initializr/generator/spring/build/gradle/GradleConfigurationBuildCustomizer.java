/*
 * Copyright 2012 - present the original author or authors.
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
		boolean providedRuntimeUsed = build.dependencies()
			.items()
			.anyMatch((dependency) -> DependencyScope.PROVIDED_RUNTIME.equals(dependency.getScope()));
		boolean war = build.plugins().values().anyMatch((plugin) -> plugin.getId().equals("war"));
		if (providedRuntimeUsed && !war) {
			build.configurations().add("providedRuntime");
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
