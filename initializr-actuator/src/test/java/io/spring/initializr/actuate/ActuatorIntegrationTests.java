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

package io.spring.initializr.actuate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import groovy.json.JsonSlurper;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class ActuatorIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	private final JsonSlurper slurper = new JsonSlurper();

	@Test
	public void infoHasExternalProperties() {
		String body = getRestTemplate().getForObject(createUrl("/info"), String.class);
		assertTrue("Wrong body:\n" + body, body.contains("\"spring-boot\""));
		assertTrue("Wrong body:\n" + body,
				body.contains("\"version\":\"1.1.4.RELEASE\""));
	}

	@Test
	public void metricsAvailableByDefault() {
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web&style=jpa");
		Map<String, Integer> result = metricsEndpoint();
		Integer requests = result.get("counter.initializr.requests");
		Integer packaging = result.get("counter.initializr.packaging.jar");
		Integer javaVersion = result.get("counter.initializr.java_version.1_8");
		Integer webDependency = result.get("counter.initializr.dependency.web");
		Integer jpaDependency = result.get("counter.initializr.dependency.jpa");

		// No jpa dep this time
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web");

		Map<String, Integer> updatedResult = metricsEndpoint();
		assertEquals("Number of request should have increased", requests + 1,
				updatedResult.get("counter.initializr.requests").intValue());
		assertEquals("jar packaging metric should have increased", packaging + 1,
				updatedResult.get("counter.initializr.packaging.jar").intValue());
		assertEquals("java version metric should have increased", javaVersion + 1,
				updatedResult.get("counter.initializr.java_version.1_8").intValue());
		assertEquals("web dependency metric should have increased", webDependency + 1,
				updatedResult.get("counter.initializr.dependency.web").intValue());
		assertEquals("jpa dependency metric should not have increased", jpaDependency,
				updatedResult.get("counter.initializr.dependency.jpa"));
	}

	private Map<String, Integer> metricsEndpoint() {
		return parseJson(getRestTemplate().getForObject(createUrl("/metrics"), String.class));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Integer> parseJson(String content) {
		return (Map<String, Integer>) slurper.parseText(content);
	}

}
