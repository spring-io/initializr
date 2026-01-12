/*
 * Copyright 2012 - present the original author or authors.
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

import java.util.List;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrConfiguration.Env;
import io.spring.initializr.metadata.InitializrMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link InitializrMetadataUpdateStrategy} that refreshes the metadata with the latest
 * state of the Spring Boot project.
 *
 * @author Stephane Nicoll
 * @see Env#getSpringBootMetadataUrl()
 */
public class SpringIoInitializrMetadataUpdateStrategy implements InitializrMetadataUpdateStrategy {

	private static final Log logger = LogFactory.getLog(SpringIoInitializrMetadataUpdateStrategy.class);

	private final RestTemplate restTemplate;

	private final JsonMapper jsonMapper;

	public SpringIoInitializrMetadataUpdateStrategy(RestTemplate restTemplate, JsonMapper jsonMapper) {
		this.restTemplate = restTemplate;
		this.jsonMapper = jsonMapper;
	}

	@Override
	public InitializrMetadata update(InitializrMetadata current) {
		String url = current.getConfiguration().getEnv().getSpringBootMetadataUrl();
		List<DefaultMetadataElement> bootVersions = fetchSpringBootVersions(url);
		if (bootVersions != null && !bootVersions.isEmpty()) {
			if (bootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
				// No default specified
				bootVersions.get(0).setDefault(true);
			}
			current.updateSpringBootVersions(bootVersions);
		}
		return current;
	}

	/**
	 * Fetch the available Spring Boot versions using the specified service url.
	 * @param url the url to the spring-boot project metadata
	 * @return the spring boot versions metadata or {@code null} if it could not be
	 * retrieved
	 */
	protected @Nullable List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
		if (StringUtils.hasText(url)) {
			try {
				logger.info("Fetching Spring Boot metadata from " + url);
				return new SpringBootMetadataReader(this.jsonMapper, this.restTemplate, url).getBootVersions();
			}
			catch (Exception ex) {
				logger.warn("Failed to fetch Spring Boot metadata", ex);
			}
		}
		return null;
	}

}
