/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.project.ProjectDescription;

/**
 * Strategy for resolving a dependency management plugin version from a platform version.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface DependencyManagementPluginVersionResolver {

	/**
	 * Resolves the dependency management plugin version to use for the generation of the
	 * project with the given {@code description}.
	 * @param description the description of the project being generated
	 * @return the corresponding version for the {@code io.spring.dependency-management}
	 * plugin
	 */
	String resolveDependencyManagementPluginVersion(ProjectDescription description);

}
