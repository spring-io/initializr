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

package io.spring.initializr.actuate.stat;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.actuate.stat.StatsProperties.Elastic;
import io.spring.initializr.web.project.ProjectRequestEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Publish stats for each project generated to an Elastic index.
 *
 * @author Stephane Nicoll
 */
public class ProjectGenerationStatPublisher {

	private static final Log logger = LogFactory
			.getLog(ProjectGenerationStatPublisher.class);

	private final ProjectRequestDocumentFactory documentFactory;

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	private URI requestUrl;

	private final RetryTemplate retryTemplate;

	public ProjectGenerationStatPublisher(ProjectRequestDocumentFactory documentFactory,
			StatsProperties statsProperties, RestTemplateBuilder restTemplateBuilder,
			RetryTemplate retryTemplate) {
		this.documentFactory = documentFactory;
		this.objectMapper = createObjectMapper();
		StatsProperties.Elastic elastic = statsProperties.getElastic();
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUri(determineEntityUrl(elastic));
		this.restTemplate = configureAuthorization(restTemplateBuilder, elastic,
				uriBuilder).build();
		this.requestUrl = uriBuilder.userInfo(null).build().toUri();
		this.retryTemplate = retryTemplate;
	}

	@EventListener
	@Async
	public void handleEvent(ProjectRequestEvent event) {
		String json = null;
		try {
			ProjectRequestDocument document = this.documentFactory.createDocument(event);
			if (logger.isDebugEnabled()) {
				logger.debug("Publishing " + document);
			}
			json = toJson(document);

			RequestEntity<String> request = RequestEntity.post(this.requestUrl)
					.contentType(MediaType.APPLICATION_JSON).body(json);

			this.retryTemplate.execute((context) -> {
				this.restTemplate.exchange(request, String.class);
				return null;
			});
		}
		catch (Exception ex) {
			logger.warn(String.format(
					"Failed to publish stat to index, document follows %n%n%s%n", json),
					ex);
		}
	}

	private String toJson(ProjectRequestDocument stats) {
		try {
			return this.objectMapper.writeValueAsString(stats);
		}
		catch (JsonProcessingException ex) {
			throw new IllegalStateException("Cannot convert to JSON", ex);
		}
	}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}

	// For testing purposes only
	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	protected void updateRequestUrl(URI requestUrl) {
		this.requestUrl = requestUrl;
	}

	private static RestTemplateBuilder configureAuthorization(
			RestTemplateBuilder restTemplateBuilder, Elastic elastic,
			UriComponentsBuilder uriComponentsBuilder) {
		String userInfo = uriComponentsBuilder.build().getUserInfo();
		if (StringUtils.hasText(userInfo)) {
			String[] credentials = userInfo.split(":");
			return restTemplateBuilder.basicAuthentication(credentials[0],
					credentials[1]);
		}
		else if (StringUtils.hasText(elastic.getUsername())) {
			return restTemplateBuilder.basicAuthentication(elastic.getUsername(),
					elastic.getPassword());
		}
		return restTemplateBuilder;
	}

	private static URI determineEntityUrl(Elastic elastic) {
		String entityUrl = elastic.getUri() + "/" + elastic.getIndexName() + "/"
				+ elastic.getEntityName();
		try {
			return new URI(entityUrl);
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException("Cannot create entity URL: " + entityUrl, ex);
		}
	}

}
