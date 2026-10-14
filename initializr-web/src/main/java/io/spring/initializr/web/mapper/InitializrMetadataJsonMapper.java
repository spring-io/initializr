/*
 * Copyright 2012 - present the original author or authors.
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
