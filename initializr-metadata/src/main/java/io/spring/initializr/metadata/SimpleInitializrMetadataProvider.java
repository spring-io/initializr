/*
 * Copyright 2012 - present the original author or authors.
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
