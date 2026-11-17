/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.support;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A strategy interface for updating the {@link InitializrMetadata metadata} on a running
 * instance.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface InitializrMetadataUpdateStrategy {

	/**
	 * Update the current {@link InitializrMetadata}.
	 * @param current the metadata to update
	 * @return the updated metadata, or {@code current} (never {@code null})
	 */
	InitializrMetadata update(InitializrMetadata current);

}
