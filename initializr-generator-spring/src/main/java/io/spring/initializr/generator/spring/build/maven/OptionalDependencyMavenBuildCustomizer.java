/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * Maven {@link BuildCustomizer} that sets the "optional" flag for a dependency.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class OptionalDependencyMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final String dependencyId;

	/**
	 * Create a new instance with the identifier for the dependency.
	 * @param dependencyId the id of the dependency
	 */
	public OptionalDependencyMavenBuildCustomizer(String dependencyId) {
		this.dependencyId = dependencyId;
	}

	@Override
	public void customize(MavenBuild build) {
		Dependency dependency = build.dependencies().get(this.dependencyId);
		if (dependency != null) {
			build.dependencies().add(this.dependencyId, MavenDependency.from(dependency).optional(true));
		}
	}

}
