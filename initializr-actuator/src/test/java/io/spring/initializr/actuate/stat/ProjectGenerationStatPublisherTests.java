/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.actuate.stat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import io.spring.initializr.actuate.stat.StatsProperties.Elastic;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Tests for {@link ProjectGenerationStatPublisher}.
 *
 * @author Stephane Nicoll
 */
class ProjectGenerationStatPublisherTests {

	private final InitializrMetadata metadata = InitializrMetadataTestBuilder
			.withDefaults().addDependencyGroup("core", "security", "validation", "aop")
			.addDependencyGroup("web", "web", "data-rest", "jersey")
			.addDependencyGroup("data", "data-jpa", "jdbc")
			.addDependencyGroup("database", "h2", "mysql").build();

	private RetryTemplate retryTemplate;

	private ProjectGenerationStatPublisher statPublisher;

	private MockRestServiceServer mockServer;

	@BeforeEach
	public void setUp() {
		configureService(createProperties());
	}

	private void configureService(StatsProperties properties) {
		ProjectRequestDocumentFactory documentFactory = new ProjectRequestDocumentFactory();
		this.retryTemplate = new RetryTemplate();
		this.statPublisher = new ProjectGenerationStatPublisher(documentFactory,
				properties, new RestTemplateBuilder(), this.retryTemplate);
		this.mockServer = MockRestServiceServer
				.createServer(this.statPublisher.getRestTemplate());
	}

	@Test
	void publishDocumentWithUserNameAndPassword() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("http://example.com/elastic");
		properties.getElastic().setUsername("foo");
		properties.getElastic().setPassword("bar");
		configureService(properties);
		testAuthorization("http://example.com/elastic/initializr/request",
				header("Authorization", "Basic Zm9vOmJhcg=="));
	}

	@Test
	void publishDocumentWithUserInfo() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://elastic:secret@es.example.com");
		configureService(properties);
		testAuthorization("https://es.example.com/initializr/request",
				header("Authorization", "Basic ZWxhc3RpYzpzZWNyZXQ="));
	}

	@Test
	void publishDocumentWithUserInfoOverridesUserNamePassword() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://elastic:secret@es.example.com");
		properties.getElastic().setUsername("another");
		properties.getElastic().setPassword("ignored-secret");
		configureService(properties);
		testAuthorization("https://es.example.com/initializr/request",
				header("Authorization", "Basic ZWxhc3RpYzpzZWNyZXQ="));
	}

	@Test
	void publishDocumentWithNoAuthentication() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://example.com/test/");
		configureService(properties);
		testAuthorization("https://example.com/test/initializr/request",
				(request) -> assertThat(request.getHeaders().containsKey("Authorization"))
						.isFalse());
	}

	private void testAuthorization(String expectedUri,
			RequestMatcher authorizationMatcher) {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.foo");
		request.setArtifactId("my-project");
		this.mockServer.expect(requestTo(expectedUri)).andExpect(method(HttpMethod.POST))
				.andExpect(authorizationMatcher)
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));
		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocument() {
		WebProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("project");
		request.setType("maven-project");
		request.setBootVersion("2.1.1.RELEASE");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("java");
		request.getParameters().put("user-agent", "curl/1.2.4");
		request.getParameters().put("cf-connecting-ip", "10.0.0.42");
		request.getParameters().put("cf-ipcountry", "BE");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-simple.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocumentWithNoClientInformation() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("test");
		request.setType("gradle-project");
		request.setBootVersion("2.1.0.RELEASE");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("java");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-no-client.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocumentWithInvalidType() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("test");
		request.setType("not-a-type");
		request.setBootVersion("2.1.0.RELEASE");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("java");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-type.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocumentWithInvalidLanguage() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("test");
		request.setType("gradle-project");
		request.setBootVersion("2.1.0.RELEASE");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("c");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-language.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocumentWithInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("test");
		request.setType("gradle-project");
		request.setBootVersion("2.1.0.RELEASE");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("java");
		request.setJavaVersion("1.2");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-java-version.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void publishDocumentWithInvalidDependencies() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("test");
		request.setType("gradle-project");
		request.setBootVersion("2.1.0.RELEASE");
		request.setDependencies(Arrays.asList("invalid-2", "web", "invalid-1"));
		request.setLanguage("java");

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-dependencies.json"))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void recoverFromError() {
		ProjectRequest request = createProjectRequest();

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CREATED)
						.body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		handleEvent(request);
		this.mockServer.verify();
	}

	@Test
	void fatalErrorOnlyLogs() {
		ProjectRequest request = createProjectRequest();
		this.retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2,
				Collections.singletonMap(Exception.class, true)));

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("http://example.com/elastic/initializr/request"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		handleEvent(request);
		this.mockServer.verify();
	}

	private WebProjectRequest createProjectRequest() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

	private void handleEvent(ProjectRequest request) {
		this.statPublisher.handleEvent(new ProjectGeneratedEvent(request, this.metadata));
	}

	private static String mockResponse(String id, boolean created) {
		return "{\"_index\":\"initializr\",\"_type\":\"request\",\"_id\":\"" + id
				+ "\",\"_version\":1,\"_shards\""
				+ ":{\"total\":1,\"successful\":1,\"failed\":0},\"created\":" + created
				+ "}";
	}

	private static StatsProperties createProperties() {
		StatsProperties properties = new StatsProperties();
		Elastic elastic = properties.getElastic();
		elastic.setUri("http://example.com/elastic");
		return properties;
	}

	private static RequestMatcher json(String location) {
		return (request) -> {
			MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
			assertJsonContent(readJson(location), mockRequest.getBodyAsString());
		};
	}

	private static String readJson(String location) {
		try {
			try (InputStream in = new ClassPathResource(location).getInputStream()) {
				return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException("Fail to read json from " + location, ex);
		}
	}

	private static void assertJsonContent(String expected, String actual) {
		try {
			JSONAssert.assertEquals(expected, actual, new CustomComparator(
					JSONCompareMode.STRICT,
					Customization.customization("generationTimestamp", (o1, o2) -> {
						Instant timestamp = Instant.ofEpochMilli((long) o1);
						return timestamp
								.isAfter(Instant.now().minus(2, ChronoUnit.SECONDS))
								&& timestamp.isBefore(Instant.now());
					})));
		}
		catch (JSONException ex) {
			throw new AssertionError(
					"Failed to parse expected or actual JSON request content", ex);
		}
	}

}
