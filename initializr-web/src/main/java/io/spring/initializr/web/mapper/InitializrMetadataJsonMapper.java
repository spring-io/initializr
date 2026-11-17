/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.mapper;

import io.spring.initializr.metadata.InitializrMetadata;
import org.jspecify.annotations.Nullable;

/**
 * Generate a JSON representation of the metadata.
 *
 * @author Stephane Nicoll
 */
public interface InitializrMetadataJsonMapper {

	/**
	 * Write a json representation of the specified metadata.
	 * @param metadata the initializr metadata
	 * @param appUrl the app URL
	 * @return the JSON representation
	 */
	String write(InitializrMetadata metadata, @Nullable String appUrl);

}
