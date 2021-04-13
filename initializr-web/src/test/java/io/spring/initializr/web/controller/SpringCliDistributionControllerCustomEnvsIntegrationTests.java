/*
 * Copyright 2012-2021 the original author or authors.
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
public class SpringCliDistributionControllerCustomEnvsIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	void downloadCliWithCustomRepository() throws Exception {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl("/spring"), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/lib-release/org/springframework/boot/spring-boot-cli/2.4.4/spring-boot-cli-2.4.4-bin.zip";
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

}
