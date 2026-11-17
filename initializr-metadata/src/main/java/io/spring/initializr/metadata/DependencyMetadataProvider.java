/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.metadata;

import io.spring.initializr.generator.version.Version;

/**
 * Provide the {@link DependencyMetadata} for a given spring boot version.
 *
 * @author Stephane Nicoll
 */
public interface DependencyMetadataProvider {

	/**
	 * Return the dependency metadata to use for the specified {@code bootVersion}.
	 * @param metadata the intializr metadata
	 * @param bootVersion the Spring Boot version
	 * @return the dependency metadata
	 */
	DependencyMetadata get(InitializrMetadata metadata, Version bootVersion);

}
