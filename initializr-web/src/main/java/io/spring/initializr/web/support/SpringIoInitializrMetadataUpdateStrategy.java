/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link InitializrMetadataUpdateStrategy} that refreshes the metadata with the latest
 * state of the Spring Boot project.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 * @see Env#getSpringBootMetadataUrl()
 */
public class SpringIoInitializrMetadataUpdateStrategy implements InitializrMetadataUpdateStrategy {

	private static final Log logger = LogFactory.getLog(SpringIoInitializrMetadataUpdateStrategy.class);

	private final RestClient restClient;

	private final JsonMapper jsonMapper;

	/**
	 * Create a new instance using the specified {@link RestClient}.
	 * @param restClient the rest client
	 * @param jsonMapper the json mapper
	 */
	public SpringIoInitializrMetadataUpdateStrategy(RestClient restClient, JsonMapper jsonMapper) {
		this.restClient = restClient;
		this.jsonMapper = jsonMapper;
	}

	/**
	 * Create a new instance using the specified {@link RestTemplate}.
	 * @param restTemplate the rest template
	 * @param jsonMapper the json mapper
	 * @deprecated in favor of
	 * {@link #SpringIoInitializrMetadataUpdateStrategy(RestClient, JsonMapper)}
	 */
	@Deprecated(forRemoval = true)
	@SuppressWarnings("removal")
	public SpringIoInitializrMetadataUpdateStrategy(RestTemplate restTemplate, JsonMapper jsonMapper) {
		this(RestClient.create(restTemplate), jsonMapper);
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
				return new SpringBootMetadataReader(this.jsonMapper, this.restClient, url).getBootVersions();
			}
			catch (Exception ex) {
				logger.warn("Failed to fetch Spring Boot metadata", ex);
			}
		}
		return null;
	}

}
