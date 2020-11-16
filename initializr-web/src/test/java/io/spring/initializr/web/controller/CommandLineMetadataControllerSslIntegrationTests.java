/*
 * Copyright 2012-2020 the original author or authors.
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

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommandLineMetadataController} with {@code forceSsl}
 * enabled.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-ssl" })
public class CommandLineMetadataControllerSslIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void forceSsl() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		String body = response.getBody();
		assertThat(body).as("Must force https").contains("https://start.example.com/");
		assertThat(body).as("Must force https").doesNotContain("http://");
	}

}
