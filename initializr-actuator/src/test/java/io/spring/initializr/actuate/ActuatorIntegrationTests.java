/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate;

import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;
import org.junit.jupiter.api.Test;

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
@SpringBootTest(classes = Config.class, webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = "management.endpoints.web.exposure.include=info")
class ActuatorIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Test
	void infoHasExternalProperties() {
		String body = getRestTemplate().getForObject(createUrl("/actuator/info"),
				String.class);
		assertThat(body).contains("\"spring-boot\"");
		assertThat(body).contains("\"version\":\"2.1.4.RELEASE\"");
	}

}
