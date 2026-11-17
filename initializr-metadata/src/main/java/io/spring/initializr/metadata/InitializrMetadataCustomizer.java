/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
