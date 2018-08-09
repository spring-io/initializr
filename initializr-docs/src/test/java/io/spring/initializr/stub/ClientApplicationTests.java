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

package io.spring.initializr.stub;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "io.spring.initializr:initializr-web:${project.version}", workOffline = true)
// tag::test[]
public class ClientApplicationTests {

	@Autowired
	private StubFinder stubFinder;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void testCurrentMetadata() {
		RequestEntity<Void> request = RequestEntity.get(createUri("/"))
				.accept(MediaType.valueOf("application/vnd.initializr.v2.1+json"))
				.build();

		ResponseEntity<String> response = this.restTemplate.exchange(request,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		// other assertions here
	}

	private URI createUri(String path) {
		String url = this.stubFinder.findStubUrl("initializr-web").toString();
		return URI.create(url + path);
	}

	@TestConfiguration
	static class Config {

		@Bean
		public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}

	}

}
// end::test[]
