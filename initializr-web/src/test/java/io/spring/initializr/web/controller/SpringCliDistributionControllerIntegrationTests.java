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

import java.net.URI;
import java.net.URISyntaxException;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpringCliDistributionController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class SpringCliDistributionControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void downloadCli() throws Exception {
		assertSpringCliRedirect("/spring", "zip");
	}

	@Test
	void downloadCliAsZip() throws Exception {
		assertSpringCliRedirect("/spring.zip", "zip");
	}

	@Test
	void downloadCliAsTarGz() throws Exception {
		assertSpringCliRedirect("/spring.tar.gz", "tar.gz");
	}

	@Test
	void downloadCliAsTgz() throws Exception {
		assertSpringCliRedirect("/spring.tgz", "tar.gz");
	}

	@Test
	void installer() {
		ResponseEntity<String> response = getRestTemplate().getForEntity(createUrl("install.sh"), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	private void assertSpringCliRedirect(String context, String extension) throws URISyntaxException {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl(context), Object.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.1.4.RELEASE/spring-boot-cli-2.1.4.RELEASE-bin."
				+ extension;
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

}
