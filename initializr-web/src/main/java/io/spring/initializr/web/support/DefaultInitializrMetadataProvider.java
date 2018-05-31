/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.support;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * A default {@link InitializrMetadataProvider} that is able to refresh the metadata with
 * the status of the main spring.io site.
 *
 * @author Stephane Nicoll
 */
public class DefaultInitializrMetadataProvider implements InitializrMetadataProvider {

	private static final Logger log = LoggerFactory
			.getLogger(DefaultInitializrMetadataProvider.class);

	private final InitializrMetadata metadata;

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	public DefaultInitializrMetadataProvider(InitializrMetadata metadata,
			ObjectMapper objectMapper, RestTemplate restTemplate) {
		this.metadata = metadata;
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}

	@Override
	@Cacheable(value = "initializr.metadata", key = "'metadata'")
	public InitializrMetadata get() {
		updateInitializrMetadata(this.metadata);
		return this.metadata;
	}

	protected void updateInitializrMetadata(InitializrMetadata metadata) {
		List<DefaultMetadataElement> bootVersions = fetchBootVersions();
		if (bootVersions != null && !bootVersions.isEmpty()) {
			if (bootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
				// No default specified
				bootVersions.get(0).setDefault(true);
			}
			metadata.updateSpringBootVersions(bootVersions);
		}
	}

	protected List<DefaultMetadataElement> fetchBootVersions() {
		String url = this.metadata.getConfiguration().getEnv().getSpringBootMetadataUrl();
		if (StringUtils.hasText(url)) {
			try {
				log.info("Fetching boot metadata from {}", url);
				return new SpringBootMetadataReader(this.objectMapper, this.restTemplate,
						url).getBootVersions();
			}
			catch (Exception ex) {
				log.warn("Failed to fetch spring boot metadata", ex);
			}
		}
		return null;
	}

}
