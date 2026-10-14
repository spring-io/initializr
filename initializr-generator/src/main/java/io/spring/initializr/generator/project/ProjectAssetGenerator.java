/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.io.IOException;

/**
 * Generate project assets using a {@link ProjectGenerationContext}.
 *
 * @param <T> the type that gathers the project assets
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectAssetGenerator<T> {

	/**
	 * Generate project assets using the specified {@link ProjectGenerationContext}.
	 * @param context the context to use
	 * @return the type that gathers the project assets
	 * @throws IOException if writing project assets failed
	 */
	T generate(ProjectGenerationContext context) throws IOException;

}
