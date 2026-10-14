/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

/**
 * Provide the {@link InitializrMetadata} to use.
 *
 * @author Stephane Nicoll
 */
public interface InitializrMetadataProvider {

	/**
	 * Return the metadata to use. Rather than keeping a handle to a particular instance,
	 * implementations may decide to refresh or recompute the metadata if necessary.
	 * @return the initializr metadata
	 */
	InitializrMetadata get();

}
