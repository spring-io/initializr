/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
