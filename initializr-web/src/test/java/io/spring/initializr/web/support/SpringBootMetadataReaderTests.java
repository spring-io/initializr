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

package io.spring.initializr.web.support;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Stephane Nicoll
 * @author Dave Syer
 */
public class SpringBootMetadataReaderTests {

	private final InitializrMetadata metadata =
			InitializrMetadataBuilder.create().build();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final RestTemplate restTemplate = new RestTemplate();

	private final MockRestServiceServer server =
			MockRestServiceServer.bindTo(restTemplate).build();

	@Test
	public void readAvailableVersions() throws IOException {
		server.expect(requestTo("https://spring.io/project_metadata/spring-boot"))
				.andRespond(withSuccess(
						new ClassPathResource("metadata/sagan/spring-boot.json"),
						MediaType.APPLICATION_JSON));
		List<DefaultMetadataElement> versions = new SpringBootMetadataReader(objectMapper,
				restTemplate, metadata.getConfiguration().getEnv()
				.getSpringBootMetadataUrl()).getBootVersions();
		assertNotNull("spring boot versions should not be null", versions);
		AtomicBoolean defaultFound = new AtomicBoolean(false);
		versions.forEach(it -> {
			assertNotNull("Id must be set", it.getId());
			assertNotNull("Name must be set", it.getName());
			if (it.isDefault()) {
				if (defaultFound.get()) {
					fail("One default version was already found " + it.getId());
				}
				defaultFound.set(true);
			}
		});
		server.verify();
	}

}
