/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.stub;

import java.net.URI;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// tag::test[]
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "io.spring.initializr:initializr-web:${project.version}", stubsMode = StubsMode.LOCAL)
class ClientApplicationTests {

	@Autowired
	private StubFinder stubFinder;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	void testCurrentMetadata() {
		RequestEntity<Void> request = RequestEntity.get(createUri("/"))
			.accept(MediaType.valueOf("application/vnd.initializr.v2.1+json"))
			.build();

		ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
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
		RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}

	}

}
// end::test[]
