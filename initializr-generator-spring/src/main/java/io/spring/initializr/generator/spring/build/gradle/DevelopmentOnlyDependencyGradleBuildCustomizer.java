/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * Gradle {@link BuildCustomizer} that sets the "developmentOnly" configuration for a
 * dependency.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class DevelopmentOnlyDependencyGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final String dependencyId;

	/**
	 * Create a new instance with the identifier for the dependency.
	 * @param dependencyId the id of the dependency
	 */
	public DevelopmentOnlyDependencyGradleBuildCustomizer(String dependencyId) {
		this.dependencyId = dependencyId;
	}

	@Override
	public void customize(GradleBuild build) {
		Dependency dependency = build.dependencies().get(this.dependencyId);
		if (dependency != null) {
			build.dependencies()
				.add(this.dependencyId, GradleDependency.from(dependency).configuration("developmentOnly"));
		}
	}

}
