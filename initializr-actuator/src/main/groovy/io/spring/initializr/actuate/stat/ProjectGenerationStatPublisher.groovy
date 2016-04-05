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

package io.spring.initializr.actuate.stat

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import io.spring.initializr.generator.ProjectRequestEvent

import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.support.RetryTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.web.client.RestTemplate

/**
 * Publish stats for each project generated to an Elastic index.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@Slf4j
class ProjectGenerationStatPublisher {

	private final ProjectRequestDocumentFactory documentFactory
	private final StatsProperties statsProperties
	private final ObjectMapper objectMapper
	private final RestTemplate restTemplate
	private final RetryTemplate retryTemplate

	ProjectGenerationStatPublisher(ProjectRequestDocumentFactory documentFactory,
								   StatsProperties statsProperties,
								   RetryTemplate retryTemplate) {
		this.documentFactory = documentFactory
		this.statsProperties = statsProperties
		this.objectMapper = createObjectMapper()
		this.restTemplate = new BasicAuthRestTemplate(
				statsProperties.elastic.username, statsProperties.elastic.password)
		this.retryTemplate = retryTemplate
	}

	@EventListener
	@Async
	void handleEvent(ProjectRequestEvent event) {
		String json = null
		try {
			ProjectRequestDocument document = documentFactory.createDocument(event)
			if (log.isDebugEnabled()) {
				log.debug("Publishing $document")
			}
			json = toJson(document)

			RequestEntity<String> request = RequestEntity
					.post(this.statsProperties.elastic.entityUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.body(json)

			this.retryTemplate.execute(new RetryCallback<Void, RuntimeException>() {
				@Override
				Void doWithRetry(RetryContext context) {
					restTemplate.exchange(request, String)
					return null
				}
			})
		} catch (Exception ex) {
			log.warn(String.format(
					"Failed to publish stat to index, document follows %n%n%s%n", json), ex)
		}
	}

	private String toJson(ProjectRequestDocument stats) {
		this.objectMapper.writeValueAsString(stats)
	}

	private static ObjectMapper createObjectMapper() {
		def mapper = new ObjectMapper()
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
		mapper
	}

}
