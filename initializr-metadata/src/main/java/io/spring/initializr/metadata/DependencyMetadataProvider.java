/*
 * Copyright 2012 - present the original author or authors.
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
