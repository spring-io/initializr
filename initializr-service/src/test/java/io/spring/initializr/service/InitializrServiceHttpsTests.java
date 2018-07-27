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

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link InitializrService} that force https.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "initializr.env.force-ssl=true")
@AutoConfigureCache
public class InitializrServiceHttpsTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int localPort;

	@Test
	public void httpCallRedirectsToHttps() {
		RequestEntity<Void> request = RequestEntity.get(URI.create("/"))
				.accept(MediaType.TEXT_HTML).build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(response.getHeaders().getLocation()).isEqualTo(
				URI.create(String.format("https://localhost:%s/", this.localPort)));
	}

	@Test
	public void securedProxiedCallDoesNotRedirect() {
		RequestEntity<Void> request = RequestEntity.get(URI.create("/"))
				.header("X-Forwarded-Proto", "https").accept(MediaType.TEXT_HTML).build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
