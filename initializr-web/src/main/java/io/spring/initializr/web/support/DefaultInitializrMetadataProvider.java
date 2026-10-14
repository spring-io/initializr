/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.support;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.cache.annotation.Cacheable;

/**
 * A default {@link InitializrMetadataProvider} that caches the {@link InitializrMetadata
 * metadata} and invokes a {@link InitializrMetadataUpdateStrategy} whenever the cache
 * expires.
 *
 * @author Stephane Nicoll
 */
public class DefaultInitializrMetadataProvider implements InitializrMetadataProvider {

	private InitializrMetadata metadata;

	private final InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy;

	public DefaultInitializrMetadataProvider(InitializrMetadata metadata,
			InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy) {
		this.metadata = metadata;
		this.initializrMetadataUpdateStrategy = initializrMetadataUpdateStrategy;
	}

	@Override
	@Cacheable(value = "initializr.metadata", key = "'metadata'")
	public InitializrMetadata get() {
		this.metadata = this.initializrMetadataUpdateStrategy.update(this.metadata);
		return this.metadata;
	}

}
