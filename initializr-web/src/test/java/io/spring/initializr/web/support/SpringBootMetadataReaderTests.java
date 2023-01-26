/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.web.support;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link SpringBootMetadataReader}.
 *
 * @author Stephane Nicoll
 * @author Dave Syer
 */
class SpringBootMetadataReaderTests {

	private final InitializrMetadata metadata = InitializrMetadataBuilder.create().build();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final RestTemplate restTemplate = new RestTemplate();

	private final MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate).build();

	@Test
	void readAvailableVersions() throws IOException {
		this.server.expect(requestTo("https://api.spring.io/projects/spring-boot/releases")).andRespond(
				withSuccess(new ClassPathResource("metadata/springio/spring-boot.json"), MediaType.APPLICATION_JSON));
		List<DefaultMetadataElement> versions = new SpringBootMetadataReader(this.objectMapper, this.restTemplate,
				this.metadata.getConfiguration().getEnv().getSpringBootMetadataUrl()).getBootVersions();
		assertThat(versions).hasSize(7);
		assertSpringBootVersion(versions.get(0), "3.0.2-SNAPSHOT", "3.0.2 (SNAPSHOT)", false);
		assertSpringBootVersion(versions.get(1), "3.0.1", "3.0.1", true);
		assertSpringBootVersion(versions.get(2), "2.7.8-SNAPSHOT", "2.7.8 (SNAPSHOT)", false);
		assertSpringBootVersion(versions.get(3), "2.7.7", "2.7.7", false);
		assertSpringBootVersion(versions.get(4), "2.6.14", "2.6.14", false);
		assertSpringBootVersion(versions.get(5), "2.5.14", "2.5.14", false);
		assertSpringBootVersion(versions.get(6), "2.4.13", "2.4.13", false);
		this.server.verify();
	}

	@Test
	void readAvailableVersionsWithInvalidVersion() throws IOException {
		this.server.expect(requestTo("https://api.spring.io/projects/spring-boot/releases"))
				.andRespond(withSuccess(new ClassPathResource("metadata/springio/spring-boot-invalid-version.json"),
						MediaType.APPLICATION_JSON));
		List<DefaultMetadataElement> versions = new SpringBootMetadataReader(this.objectMapper, this.restTemplate,
				this.metadata.getConfiguration().getEnv().getSpringBootMetadataUrl()).getBootVersions();
		assertThat(versions).hasSize(1);
		assertSpringBootVersion(versions.get(0), "3.0.1", "3.0.1", true);
		this.server.verify();
	}

	private void assertSpringBootVersion(DefaultMetadataElement actual, String id, String name,
			boolean defaultVersion) {
		assertThat(actual.getId()).isEqualTo(id);
		assertThat(actual.getName()).isEqualTo(name);
		assertThat(actual.isDefault()).isEqualTo(defaultVersion);
	}

}
