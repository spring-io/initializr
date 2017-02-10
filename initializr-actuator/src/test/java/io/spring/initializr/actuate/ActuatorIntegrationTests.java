/*
 * Copyright 2012-2017 the original author or authors.
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

import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.json.JSONObject;
import org.junit.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class ActuatorIntegrationTests
		extends AbstractFullStackInitializrIntegrationTests {

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
		JSONObject result = metricsEndpoint();
		int requests = result.getInt("counter.initializr.requests");
		int packaging = result.getInt("counter.initializr.packaging.jar");
		int javaVersion = result.getInt("counter.initializr.java_version.1_8");
		int webDependency = result.getInt("counter.initializr.dependency.web");
		int jpaDependency = result.getInt("counter.initializr.dependency.data-jpa");

		// No jpa dep this time
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web");

		JSONObject updatedResult = metricsEndpoint();
		assertEquals("Number of request should have increased", requests + 1,
				updatedResult.getInt("counter.initializr.requests"));
		assertEquals("jar packaging metric should have increased", packaging + 1,
				updatedResult.getInt("counter.initializr.packaging.jar"));
		assertEquals("java version metric should have increased", javaVersion + 1,
				updatedResult.getInt("counter.initializr.java_version.1_8"));
		assertEquals("web dependency metric should have increased", webDependency + 1,
				updatedResult.getInt("counter.initializr.dependency.web"));
		assertEquals("jpa dependency metric should not have increased", jpaDependency,
				updatedResult.getInt("counter.initializr.dependency.data-jpa"));
	}

	private JSONObject metricsEndpoint() {
		return parseJson(getRestTemplate().getForObject(createUrl("/metrics"), String.class));
	}

	private JSONObject parseJson(String content) {
		return new JSONObject(content);
	}

}
