/*
 * Copyright 2012-2018 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;
import org.junit.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for actuator specific features.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@SpringBootTest(classes = Config.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = "management.endpoints.web.exposure.include=info,metrics")
public class ActuatorIntegrationTests
		extends AbstractFullStackInitializrIntegrationTests {

	@Test
	public void infoHasExternalProperties() {
		String body = getRestTemplate().getForObject(createUrl("/actuator/info"),
				String.class);
		assertThat(body).contains("\"spring-boot\"");
		assertThat(body).contains("\"version\":\"1.1.4.RELEASE\"");
	}

	@Test
	public void metricsAreRegistered() {
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web&style=jpa");
		JsonNode result = metricsEndpoint();
		JsonNode names = result.get("names");
		List<String> metrics = new ArrayList<>();
		for (JsonNode name : names) {
			metrics.add(name.textValue());
		}
		assertThat(metrics).contains("initializr.requests", "initializr.packaging.jar",
				"initializr.java_version.1_8", "initializr.dependency.web",
				"initializr.dependency.data-jpa");

		int requests = metricValue("initializr.requests");
		int packaging = metricValue("initializr.packaging.jar");
		int javaVersion = metricValue("initializr.java_version.1_8");
		int webDependency = metricValue("initializr.dependency.web");
		int jpaDependency = metricValue("initializr.dependency.data-jpa");

		// No jpa dep this time
		downloadZip("/starter.zip?packaging=jar&javaVersion=1.8&style=web");

		assertThat(metricValue("initializr.requests"))
				.as("Number of request should have increased").isEqualTo(requests + 1);
		assertThat(metricValue("initializr.packaging.jar"))
				.as("jar packaging metric should have increased")
				.isEqualTo(packaging + 1);
		assertThat(metricValue("initializr.java_version.1_8"))
				.as("java version metric should have increased")
				.isEqualTo(javaVersion + 1);
		assertThat(metricValue("initializr.dependency.web"))
				.as("web dependency metric should have increased")
				.isEqualTo(webDependency + 1);
		assertThat(metricValue("initializr.dependency.data-jpa"))
				.as("jpa dependency metric should not have increased")
				.isEqualTo(jpaDependency);
	}

	private JsonNode metricsEndpoint() {
		return parseJson(getRestTemplate().getForObject(createUrl("/actuator/metrics"),
				String.class));
	}

	private int metricValue(String metric) {
		JsonNode root = parseJson(getRestTemplate()
				.getForObject(createUrl("/actuator/metrics/" + metric), String.class));
		JsonNode measurements = root.get("measurements");
		assertThat(measurements.isArray());
		assertThat(measurements.size()).isEqualTo(1);
		JsonNode measurement = measurements.get(0);
		return measurement.get("value").intValue();
	}

}
