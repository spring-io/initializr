/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.metadata;

/**
 * A simple {@link InitializrMetadataProvider} implementation.
 *
 * @author Stephane Nicoll
 */
public class SimpleInitializrMetadataProvider implements InitializrMetadataProvider {

	private final InitializrMetadata metadata;

	public SimpleInitializrMetadataProvider(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public InitializrMetadata get() {
		return this.metadata;
	}

}
