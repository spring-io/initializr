/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
