/*
 * Copyright 2012 - present the original author or authors.
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
