/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
