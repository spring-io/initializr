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

package io.spring.initializr.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic smoke tests for {@link InitializrService}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InitializrServiceSmokeTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	@Test
	public void metadataCanBeSerialized() throws URISyntaxException, IOException {
		RequestEntity<Void> request = RequestEntity.get(new URI("/"))
				.accept(MediaType.parseMediaType("application/vnd.initializr.v2.1+json"))
				.build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		new ObjectMapper().readTree(response.getBody());
	}

	@Test
	public void configurationCanBeSerialized() throws URISyntaxException {
		RequestEntity<Void> request = RequestEntity.get(new URI("/metadata/config"))
				.accept(MediaType.APPLICATION_JSON).build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		InitializrMetadata actual = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ByteArrayResource(response.getBody().getBytes()))
				.build();
		assertThat(actual).isNotNull();
		InitializrMetadata expected = this.metadataProvider.get();
		assertThat(actual.getDependencies().getAll().size())
				.isEqualTo(expected.getDependencies().getAll().size());
		assertThat(actual.getConfiguration().getEnv().getBoms().size())
				.isEqualTo(expected.getConfiguration().getEnv().getBoms().size());
	}

}
