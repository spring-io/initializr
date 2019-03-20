/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
