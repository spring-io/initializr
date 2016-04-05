/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web.support

import groovy.util.logging.Slf4j
import io.spring.initializr.metadata.DefaultMetadataElement
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.InitializrMetadataProvider

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable

/**
 * A default {@link InitializrMetadataProvider} that is able to refresh
 * the metadata with the status of the main spring.io site.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@Slf4j
class DefaultInitializrMetadataProvider implements InitializrMetadataProvider {

	private final InitializrMetadata metadata

	@Autowired
	DefaultInitializrMetadataProvider(InitializrMetadata metadata) {
		this.metadata = metadata
	}

	@Override
	@Cacheable(value = 'initializr', key = "'metadata'")
	InitializrMetadata get() {
		def bootVersions = fetchBootVersions()
		if (bootVersions) {
			metadata.bootVersions.content.clear()
			metadata.bootVersions.content.addAll(bootVersions)
		}
		metadata
	}

	protected List<DefaultMetadataElement> fetchBootVersions() {
		def url = metadata.configuration.env.springBootMetadataUrl
		if (url) {
			try {
				log.info("Fetching boot metadata from $url")
				return new SpringBootMetadataReader(url).bootVersions
			} catch (Exception e) {
				log.warn('Failed to fetch spring boot metadata', e)
			}
		}
		null
	}

}
