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

package io.spring.initializr.actuate

import groovy.json.JsonSlurper
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests
import org.junit.Test

import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class ActuatorIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	private final def slurper = new JsonSlurper()

	@Test
	void infoHasExternalProperties() {
		def body = restTemplate.getForObject(createUrl('/info'), String)
		assertTrue("Wrong body:\n$body", body.contains('"spring-boot"'))
		assertTrue("Wrong body:\n$body", body.contains('"version":"1.1.4.RELEASE"'))
	}

	@Test
	void metricsAvailableByDefault() {
		downloadZip('/starter.zip?packaging=jar&javaVersion=1.8&style=web&style=jpa')
		def result = metricsEndpoint()
		def requests = result['counter.initializr.requests']
		def packaging = result['counter.initializr.packaging.jar']
		def javaVersion = result['counter.initializr.java_version.1_8']
		def webDependency = result['counter.initializr.dependency.web']
		def jpaDependency = result['counter.initializr.dependency.jpa']

		downloadZip('/starter.zip?packaging=jar&javaVersion=1.8&style=web') // No jpa dep this time

		def updatedResult = metricsEndpoint()
		assertEquals 'Number of request should have increased',
				requests + 1, updatedResult['counter.initializr.requests']
		assertEquals 'jar packaging metric should have increased',
				packaging + 1, updatedResult['counter.initializr.packaging.jar']
		assertEquals 'java version metric should have increased',
				javaVersion + 1, updatedResult['counter.initializr.java_version.1_8']
		assertEquals 'web dependency metric should have increased',
				webDependency + 1, updatedResult['counter.initializr.dependency.web']
		assertEquals 'jpa dependency metric should not have increased',
				jpaDependency, updatedResult['counter.initializr.dependency.jpa']
	}

	private def metricsEndpoint() {
		parseJson(restTemplate.getForObject(createUrl('/metrics'), String))
	}

	private def parseJson(String content) {
		slurper.parseText(content)
	}

}
