/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.net.URI;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpringCliDistributionController} with custom defaults.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-env" })
class SpringCliDistributionControllerCustomEnvsIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void downloadCliWithCustomRepository() throws Exception {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl("/spring"), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/lib-release/org/springframework/boot/spring-boot-cli/2.4.4/spring-boot-cli-2.4.4-bin.zip";
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

}
