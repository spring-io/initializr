package io.spring.initializr.stub;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;

/**
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
// Need a spring boot app. Doesn't really have one
@AutoConfigureStubRunner(
		// TODO: can't hardcode the version there as it must be automatically replaced when releasing
		ids = "io.spring.initializr:initializr-web:0.3.0.BUILD-SNAPSHOT",
		workOffline = true)
public class InitializrIntegrationTests {

	@Autowired
	private StubFinder stubFinder;


	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Test
	public void contextLoads() throws IOException {
		String url = stubFinder.findStubUrl("io.spring.initializr:initializr-web").toString();
		RestTemplate template = restTemplateBuilder
				.interceptors((request, body, execution) -> {
					request.getHeaders().setAccept(Collections.singletonList(
							MediaType.valueOf("application/vnd.initializr.v2+json")));
					return execution.execute(request, body);
				}).build();
		// Can we write an assert that makes more sense in the context of the doc?
		assertThat(template.getForEntity(url + "/", String.class)
				.getBody()).contains("dependencies");
	}

}
