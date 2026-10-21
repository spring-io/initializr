/*
 * Copyright 2012 - present the original author or authors.
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
		properties = { "management.endpoints.web.exposure.include=info", "management.info.env.enabled=true" })
class ActuatorIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Test
	void infoHasExternalProperties() {
		String body = bodyOf(execute("/actuator/info", null));
		assertThat(body).contains("\"spring-boot\"");
		assertThat(body).contains("\"version\":\"2.4.4\"");
	}

}
