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

package io.spring.initializr.web.support

import io.spring.initializr.metadata.DefaultMetadataElement
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Before
import org.junit.Test

import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

/**
 * @author Stephane Nicoll
 */
class DefaultInitializrMetadataProviderTests {

	private RestTemplate restTemplate

	private MockRestServiceServer mockServer

	@Before
	public void setUp() {
		restTemplate = new RestTemplate()
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void bootVersionsAreReplaced() {
		def metadata = new InitializrMetadataTestBuilder()
				.addBootVersion('0.0.9.RELEASE', true).addBootVersion('0.0.8.RELEASE', false).build()
		assertEquals '0.0.9.RELEASE', metadata.bootVersions.default.id
		def provider = new DefaultInitializrMetadataProvider(metadata, restTemplate)
		expectJson(metadata.configuration.env.springBootMetadataUrl,
				"metadata/sagan/spring-boot.json");

		def updatedMetadata = provider.get()
		assertNotNull updatedMetadata.bootVersions
		def updatedBootVersions = updatedMetadata.bootVersions.content
		assertEquals 4, updatedBootVersions.size()
		assertBootVersion(updatedBootVersions[0], '1.4.1 (SNAPSHOT)', false)
		assertBootVersion(updatedBootVersions[1], '1.4.0', true)
		assertBootVersion(updatedBootVersions[2], '1.3.8 (SNAPSHOT)', false)
		assertBootVersion(updatedBootVersions[3], '1.3.7', false)
	}

	@Test
	void defaultBootVersionIsAlwaysSet() {
		def metadata = new InitializrMetadataTestBuilder()
				.addBootVersion('0.0.9.RELEASE', true).addBootVersion('0.0.8.RELEASE', false).build()
		assertEquals '0.0.9.RELEASE', metadata.bootVersions.default.id
		def provider = new DefaultInitializrMetadataProvider(metadata, restTemplate)
		expectJson(metadata.configuration.env.springBootMetadataUrl,
				"metadata/sagan/spring-boot-no-default.json");

		def updatedMetadata = provider.get()
		assertNotNull updatedMetadata.bootVersions
		def updatedBootVersions = updatedMetadata.bootVersions.content
		assertEquals 4, updatedBootVersions.size()
		assertBootVersion(updatedBootVersions[0], '1.3.1 (SNAPSHOT)', true)
		assertBootVersion(updatedBootVersions[1], '1.3.0', false)
		assertBootVersion(updatedBootVersions[2], '1.2.6 (SNAPSHOT)', false)
		assertBootVersion(updatedBootVersions[3], '1.2.5', false)
	}

	private static void assertBootVersion(DefaultMetadataElement actual, String name, boolean defaultVersion) {
		assertEquals name, actual.name
		assertEquals defaultVersion, actual.default
	}

	private void expectJson(String url, String bodyPath) {
		HttpHeaders httpHeaders = new HttpHeaders()
		httpHeaders.setContentType(MediaType.APPLICATION_JSON)
		this.mockServer.expect(requestTo(url))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
				.body(new ClassPathResource(bodyPath))
				.headers(httpHeaders))
	}

}
