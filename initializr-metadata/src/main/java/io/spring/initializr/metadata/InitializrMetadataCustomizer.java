/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

/**
 * Strategy interface used to customize the {@link InitializrMetadata}.
 *
 * @author Dave Syer
 */
public interface InitializrMetadataCustomizer {

	/**
	 * Customize the {@link InitializrMetadata}, updating or moving around capabilities
	 * before they are validated.
	 * @param metadata the initializr metadata
	 */
	void customize(InitializrMetadata metadata);

}
