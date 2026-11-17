/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.mapper;

import io.spring.initializr.metadata.DependencyMetadata;

/**
 * Generate a JSON representation of a set of dependencies.
 *
 * @author Stephane Nicoll
 */
public interface DependencyMetadataJsonMapper {

	/**
	 * Write a json representation of the specified metadata.
	 * @param metadata the dependency metadata
	 * @return the JSON representation
	 */
	String write(DependencyMetadata metadata);

}
