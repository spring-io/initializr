/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.project.ProjectDescription;

/**
 * Strategy for resolving a Kotlin version from a platform version.
 *
 * @author Andy Wilkinson
 */
@FunctionalInterface
public interface KotlinVersionResolver {

	/**
	 * Resolves the Kotlin version to use for the generation of the project with the given
	 * {@code description}.
	 * @param description the description of the project being generated
	 * @return the corresponding Kotlin version
	 */
	String resolveKotlinVersion(ProjectDescription description);

}
