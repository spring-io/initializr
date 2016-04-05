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

import groovy.json.JsonSlurper
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests
import org.junit.Before
import org.junit.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

/**
 * Integration tests for stats processing.
 *
 * @author Stephane Nicoll
 */
@SpringApplicationConfiguration(StatsMockController.class)
@ActiveProfiles(['test-default', 'test-custom-stats'])
class MainControllerStatsIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Autowired
	private StatsMockController statsMockController

	@Autowired
	private StatsProperties statsProperties

	private final JsonSlurper slurper = new JsonSlurper()

	@Before
	public void setup() {
		this.statsMockController.stats.clear()
		// Make sure our mock is going to be invoked with the stats
		this.statsProperties.elastic.uri = "http://localhost:$port/elastic"
	}

	@Test
	void simpleProject() {
		downloadArchive('/starter.zip?groupId=com.foo&artifactId=bar&dependencies=web')
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertEquals 'com.foo', json.groupId
		assertEquals 'bar', json.artifactId
		assertEquals 1, json.dependencies.size()
		assertEquals 'web', json.dependencies[0]
	}

	@Test
	void authorizationHeaderIsSet() {
		downloadArchive('/starter.zip')
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def authorization = content.authorization
		assertNotNull 'Authorization header must be set', authorization
		assertTrue 'Wrong value for authorization header', authorization.startsWith('Basic ')
		def token = authorization.substring('Basic '.length(), authorization.size())
		def data = new String(Base64Utils.decodeFromString(token)).split(':')
		assertEquals "Wrong user from $token", 'test-user', data[0]
		assertEquals "Wrong password $token", 'test-password', data[1]
	}

	@Test
	void requestIpNotSetByDefault() {
		downloadArchive('/starter.zip?groupId=com.foo&artifactId=bar&dependencies=web')
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertFalse 'requestIp property should not be set', json.containsKey('requestIp')
	}

	@Test
	void requestIpIsSetWhenHeaderIsPresent() {
		RequestEntity<?> request = RequestEntity.get(new URI(createUrl('/starter.zip')))
				.header('X-FORWARDED-FOR', '10.0.0.123').build()
		restTemplate.exchange(request, String)
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertEquals 'Wrong requestIp', '10.0.0.123', json.requestIp
	}

	@Test
	void requestIpv4IsNotSetWhenHeaderHasGarbage() {
		RequestEntity<?> request = RequestEntity.get(new URI(createUrl('/starter.zip')))
				.header('x-forwarded-for', 'foo-bar').build()
		restTemplate.exchange(request, String)
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertFalse 'requestIpv4 property should not be set if value is not a valid IPv4',
				json.containsKey('requestIpv4')
	}

	@Test
	void requestCountryIsNotSetWhenHeaderIsSetToXX() {
		RequestEntity<?> request = RequestEntity.get(new URI(createUrl('/starter.zip')))
				.header('cf-ipcountry', 'XX').build()
		restTemplate.exchange(request, String)
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertFalse 'requestCountry property should not be set if value is set to xx',
				json.containsKey('requestCountry')
	}

	@Test
	void invalidProjectSillHasStats() {
		try {
			downloadArchive('/starter.zip?type=invalid-type')
			fail("Should have failed to generate project with invalid type")
		} catch (HttpClientErrorException ex) {
			assertEquals HttpStatus.BAD_REQUEST, ex.statusCode
		}
		assertEquals 'No stat got generated', 1, statsMockController.stats.size()
		def content = statsMockController.stats[0]

		def json = slurper.parseText(content.json)
		assertEquals 'com.example', json.groupId
		assertEquals 'demo', json.artifactId
		assertEquals true, json.invalid
		assertEquals true, json.invalidType
		assertNotNull json.errorMessage
		assertTrue json.errorMessage.contains('invalid-type')
	}

	@Test
	void errorPublishingStatsDoesNotBubbleUp() {
		this.statsProperties.elastic.uri = "http://localhost:$port/elastic-error"
		downloadArchive('/starter.zip')
		assertEquals 'No stat should be available', 0, statsMockController.stats.size()
	}


	@RestController
	static class StatsMockController {

		private final List<Content> stats = []

		@RequestMapping(path = '/elastic/test/my-entity', method = RequestMethod.POST)
		void handleProjectRequestDocument(RequestEntity<String> input) {
			def authorization = input.headers.getFirst(HttpHeaders.AUTHORIZATION)
			def content = new Content(authorization: authorization, json: input.body)
			this.stats << content
		}

		@RequestMapping(path = '/elastic-error/test/my-entity', method = RequestMethod.POST)
		void handleExpectedError() {
			throw new IllegalStateException('Expected exception')
		}

		static class Content {

			String authorization

			String json

		}

	}

}
