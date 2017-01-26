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

import io.spring.initializr.generator.ProjectGeneratedEvent
import io.spring.initializr.generator.ProjectRequest
import org.junit.Before
import org.junit.Test

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.test.web.client.MockRestServiceServer

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

/**
 * @author Stephane Nicoll
 */
class ProjectGenerationStatPublisherTests extends AbstractInitializrStatTests {

	private StatsProperties properties
	private RetryTemplate retryTemplate
	private ProjectGenerationStatPublisher statPublisher
	private MockRestServiceServer mockServer


	@Before
	public void setUp() {
		this.properties = createProperties()
		ProjectRequestDocumentFactory documentFactory =
				new ProjectRequestDocumentFactory(createProvider(metadata))
		this.retryTemplate = new RetryTemplate()
		this.statPublisher = new ProjectGenerationStatPublisher(documentFactory, properties, retryTemplate)
		mockServer = MockRestServiceServer.createServer(this.statPublisher.restTemplate);
	}

	@Test
	public void publishSimpleDocument() {
		ProjectRequest request = createProjectRequest()
		request.groupId = 'com.example.foo'
		request.artifactId = 'my-project'

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andExpect(jsonPath('$.groupId').value('com.example.foo'))
				.andExpect(jsonPath('$.artifactId').value('my-project'))
				.andRespond(withStatus(HttpStatus.CREATED)
				.body(mockResponse(UUID.randomUUID().toString(), true))
				.contentType(MediaType.APPLICATION_JSON)
		)

		this.statPublisher.handleEvent(new ProjectGeneratedEvent(request))
		mockServer.verify()
	}

	@Test
	public void recoverFromError() {
		ProjectRequest request = createProjectRequest()

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CREATED)
				.body(mockResponse(UUID.randomUUID().toString(), true))
				.contentType(MediaType.APPLICATION_JSON))

		this.statPublisher.handleEvent(new ProjectGeneratedEvent(request))
		mockServer.verify()
	}

	@Test
	public void fatalErrorOnlyLogs() {
		ProjectRequest request = createProjectRequest()
		this.retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2,
				Collections.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true)))

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

		mockServer.expect(requestTo('http://example.com/elastic/initializr/request'))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

		this.statPublisher.handleEvent(new ProjectGeneratedEvent(request))
		mockServer.verify()
	}

	private static String mockResponse(String id, boolean created) {
		'{"_index":"initializr","_type":"request","_id":"' + id + '","_version":1,"_shards"' +
				':{"total":1,"successful":1,"failed":0},"created":' + created + '}'
	}

	private static StatsProperties createProperties() {
		def properties = new StatsProperties()
		properties.elastic.uri = 'http://example.com/elastic'
		properties.elastic.username = 'foo'
		properties.elastic.password = 'bar'
		properties
	}

}
