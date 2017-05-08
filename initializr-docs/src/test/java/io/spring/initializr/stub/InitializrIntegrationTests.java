package io.spring.initializr.stub;

import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

// tag::test[]
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureWireMock(stubs = "classpath:/META-INF/io.spring.initializr/initializr-web/**/*.json", port = 0)
public class InitializrIntegrationTests {

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void testCurrentMetadata() throws IOException {
		RequestEntity<Void> request = RequestEntity.get(createUri("/"))
				.accept(MediaType.valueOf("application/vnd.initializr.v2.1+json"))
				.build();

		ResponseEntity<String> response = this.restTemplate
				.exchange(request, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		// other assertions here
	}

	private URI createUri(String path) {
		String url = "http://localhost:" + this.environment.getProperty("wiremock.server.port");
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
