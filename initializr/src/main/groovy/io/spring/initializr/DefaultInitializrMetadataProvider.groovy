/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable

/**
 * A default {@link InitializrMetadataProvider} that is able to refresh
 * the metadata with the status of the main spring.io site.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class DefaultInitializrMetadataProvider implements InitializrMetadataProvider {

	private static final Logger logger = LoggerFactory.getLogger(DefaultInitializrMetadataProvider)

	private final InitializrMetadata metadata

	@Autowired
	DefaultInitializrMetadataProvider(InitializrMetadata metadata) {
		this.metadata = metadata
	}

	@Override
	@Cacheable(value = 'initializr', key = "'metadata'")
	InitializrMetadata get() {
		List<InitializrMetadata.BootVersion> bootVersions = fetchBootVersions()
		if (bootVersions != null && !bootVersions.isEmpty()) {
			metadata.merge(bootVersions)
		}
		metadata
	}

	protected List<InitializrMetadata.BootVersion> fetchBootVersions() {
		def url = metadata.env.springBootMetadataUrl
		if (url != null) {
			try {
				logger.info('Fetching boot metadata from '+ url)
				return new SpringBootMetadataReader(url).getBootVersions()
			} catch (Exception e) {
				logger.warn('Failed to fetch spring boot metadata', e)
			}
		}
		null
	}

}
