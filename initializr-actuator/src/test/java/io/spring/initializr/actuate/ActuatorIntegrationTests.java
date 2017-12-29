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

import com.fasterxml.jackson.databind.JsonNode;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.junit.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.spring.initializr.web.AbstractInitializrIntegrationTests.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@SpringBootTest(classes = Config.class, webEnvironment = RANDOM_PORT,
		properties = "management.security.enabled=false")
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
		JsonNode result = metricsEndpoint();
		int requests = result.get("counter.initializr.requests").intValue();
		int packaging = result.get("counter.initializr.packaging.jar").intValue();
		int javaVersion = result.get("counter.initializr.java_version.1_8").intValue();
		int webDependency = result.get("counter.initializr.dependency.web").intValue();
		int jpaDependency = result.get("counter.initializr.dependency.data-jpa")
				.intValue();

		// No jpa dep this time
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web");

		JsonNode updatedResult = metricsEndpoint();
		assertEquals("Number of request should have increased", requests + 1,
				updatedResult.get("counter.initializr.requests").intValue());
		assertEquals("jar packaging metric should have increased", packaging + 1,
				updatedResult.get("counter.initializr.packaging.jar").intValue());
		assertEquals("java version metric should have increased", javaVersion + 1,
				updatedResult.get("counter.initializr.java_version.1_8").intValue());
		assertEquals("web dependency metric should have increased", webDependency + 1,
				updatedResult.get("counter.initializr.dependency.web").intValue());
		assertEquals("jpa dependency metric should not have increased", jpaDependency,
				updatedResult.get("counter.initializr.dependency.data-jpa").intValue());
	}

	private JsonNode metricsEndpoint() {
		return parseJson(getRestTemplate().getForObject(createUrl("/metrics"), String.class));
	}

}
