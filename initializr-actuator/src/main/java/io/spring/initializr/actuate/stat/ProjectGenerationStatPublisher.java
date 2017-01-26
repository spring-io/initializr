/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.spring.initializr.generator.ProjectRequestEvent;

/**
 * Publish stats for each project generated to an Elastic index.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
public class ProjectGenerationStatPublisher {

	private static final Logger log = LoggerFactory
			.getLogger(ProjectGenerationStatPublisher.class);

	private final ProjectRequestDocumentFactory documentFactory;
	private final StatsProperties statsProperties;
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	private final RetryTemplate retryTemplate;

	public ProjectGenerationStatPublisher(ProjectRequestDocumentFactory documentFactory,
			StatsProperties statsProperties, RetryTemplate retryTemplate) {
		this.documentFactory = documentFactory;
		this.statsProperties = statsProperties;
		this.objectMapper = createObjectMapper();
		this.restTemplate = new RestTemplateBuilder()
				.basicAuthorization(statsProperties.getElastic().getUsername(),
						statsProperties.getElastic().getPassword())
				.build();
		this.retryTemplate = retryTemplate;
	}

	@EventListener
	@Async
	public void handleEvent(ProjectRequestEvent event) {
		String json = null;
		try {
			ProjectRequestDocument document = documentFactory.createDocument(event);
			if (log.isDebugEnabled()) {
				log.debug("Publishing $document");
			}
			json = toJson(document);

			RequestEntity<String> request = RequestEntity
					.post(this.statsProperties.getElastic().getEntityUrl())
					.contentType(MediaType.APPLICATION_JSON).body(json);

			this.retryTemplate.execute(new RetryCallback<Void, RuntimeException>() {
				@Override
				public Void doWithRetry(RetryContext context) {
					restTemplate.exchange(request, String.class);
					return null;
				}
			});
		}
		catch (Exception ex) {
			log.warn(String.format(
					"Failed to publish stat to index, document follows %n%n%s%n", json),
					ex);
		}
	}

	private String toJson(ProjectRequestDocument stats) {
		try {
			return this.objectMapper.writeValueAsString(stats);
		}
		catch (JsonProcessingException e) {
			throw new IllegalStateException("Cannot convert to JSON", e);
		}
	}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

}
