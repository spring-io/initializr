/*
 * Copyright 2012 - present the original author or authors.
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
