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
		this.server.expect(requestTo("https://spring.io/project_metadata/spring-boot")).andRespond(
				withSuccess(new ClassPathResource("metadata/sagan/spring-boot.json"), MediaType.APPLICATION_JSON));
		List<DefaultMetadataElement> versions = new SpringBootMetadataReader(this.objectMapper, this.restTemplate,
				this.metadata.getConfiguration().getEnv().getSpringBootMetadataUrl()).getBootVersions();
		assertThat(versions).hasSize(5);
		assertSpringBootVersion(versions.get(0), "2.5.0-M1", "2.5.0 (M1)", false);
		assertSpringBootVersion(versions.get(1), "2.4.1-SNAPSHOT", "2.4.1 (SNAPSHOT)", false);
		assertSpringBootVersion(versions.get(2), "2.4.0", "2.4.0", true);
		assertSpringBootVersion(versions.get(3), "2.3.8.BUILD-SNAPSHOT", "2.3.8 (SNAPSHOT)", false);
		assertSpringBootVersion(versions.get(4), "2.3.7.RELEASE", "2.3.7", false);
		this.server.verify();
	}

	@Test
	void readAvailableVersionsWithInvalidVersion() throws IOException {
		this.server.expect(requestTo("https://spring.io/project_metadata/spring-boot")).andRespond(withSuccess(
				new ClassPathResource("metadata/sagan/spring-boot-invalid-version.json"), MediaType.APPLICATION_JSON));
		List<DefaultMetadataElement> versions = new SpringBootMetadataReader(this.objectMapper, this.restTemplate,
				this.metadata.getConfiguration().getEnv().getSpringBootMetadataUrl()).getBootVersions();
		assertThat(versions).hasSize(2);
		assertSpringBootVersion(versions.get(0), "2.5.0-M1", "2.5.0 (M1)", false);
		assertSpringBootVersion(versions.get(1), "2.4.0", "2.4.0", true);
		this.server.verify();
	}

	private void assertSpringBootVersion(DefaultMetadataElement actual, String id, String name,
			boolean defaultVersion) {
		assertThat(actual.getId()).isEqualTo(id);
		assertThat(actual.getName()).isEqualTo(name);
		assertThat(actual.isDefault()).isEqualTo(defaultVersion);
	}

}
