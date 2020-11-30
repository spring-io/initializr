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

package io.spring.initializr.actuate.stat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import io.spring.initializr.actuate.stat.StatsProperties.Elastic;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
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

	private final InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("core", "security", "validation", "aop")
			.addDependencyGroup("web", "web", "data-rest", "jersey").addDependencyGroup("data", "data-jpa", "jdbc")
			.addDependencyGroup("database", "h2", "mysql").build();

	private RetryTemplate retryTemplate;

	private ProjectGenerationStatPublisher statPublisher;

	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		configureService(createProperties());
	}

	private void configureService(StatsProperties properties) {
		ProjectRequestDocumentFactory documentFactory = new ProjectRequestDocumentFactory();
		this.retryTemplate = new RetryTemplate();
		this.statPublisher = new ProjectGenerationStatPublisher(documentFactory, properties, new RestTemplateBuilder(),
				this.retryTemplate);
		this.mockServer = MockRestServiceServer.createServer(this.statPublisher.getRestTemplate());
	}

	@Test
	void publishDocumentWithUserNameAndPassword() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://example.com/elastic");
		properties.getElastic().setUsername("foo");
		properties.getElastic().setPassword("bar");
		configureService(properties);
		testAuthorization("https://example.com/elastic/initializr/_doc/",
				header("Authorization", "Basic Zm9vOmJhcg=="));
	}

	@Test
	void publishDocumentWithUserInfo() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://elastic:secret@es.example.com");
		configureService(properties);
		testAuthorization("https://es.example.com/initializr/_doc/",
				header("Authorization", "Basic ZWxhc3RpYzpzZWNyZXQ="));
	}

	@Test
	void publishDocumentWithUserInfoOverridesUserNamePassword() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://elastic:secret@es.example.com");
		properties.getElastic().setUsername("another");
		properties.getElastic().setPassword("ignored-secret");
		configureService(properties);
		testAuthorization("https://es.example.com/initializr/_doc/",
				header("Authorization", "Basic ZWxhc3RpYzpzZWNyZXQ="));
	}

	@Test
	void publishDocumentWithNoAuthentication() {
		StatsProperties properties = new StatsProperties();
		properties.getElastic().setUri("https://example.com/test/");
		configureService(properties);
		testAuthorization("https://example.com/test/initializr/_doc/",
				(request) -> assertThat(request.getHeaders().containsKey("Authorization")).isFalse());
	}

	private void testAuthorization(String expectedUri, RequestMatcher authorizationMatcher) {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.foo");
		request.setArtifactId("my-project");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo(expectedUri)).andExpect(method(HttpMethod.POST))
				.andExpect(authorizationMatcher)
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));
		this.statPublisher.handleEvent(event);
		this.mockServer.verify();
	}

	@Test
	void publishDocument() {
		WebProjectRequest request = createProjectRequest();
		request.setGroupId("com.example.acme");
		request.setArtifactId("project");
		request.setType("maven-project");
		request.setBootVersion("2.4.1");
		request.setDependencies(Arrays.asList("web", "data-jpa"));
		request.setLanguage("java");
		request.getParameters().put("user-agent", "curl/1.2.4");
		request.getParameters().put("cf-connecting-ip", "10.0.0.42");
		request.getParameters().put("cf-ipcountry", "BE");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andExpect(json("stat/request-simple.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
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
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andExpect(json("stat/request-no-client.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
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
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-type.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
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
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-language.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
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
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-java-version.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
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
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(json("stat/request-invalid-dependencies.json", event.getTimestamp()))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
		this.mockServer.verify();
	}

	@Test
	void recoverFromError() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse(UUID.randomUUID().toString(), true))
						.contentType(MediaType.APPLICATION_JSON));

		this.statPublisher.handleEvent(event);
		this.mockServer.verify();
	}

	@Test
	void fatalErrorOnlyLogs() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, this.metadata);
		this.retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2, Collections.singletonMap(Exception.class, true)));

		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.mockServer.expect(requestTo("https://example.com/elastic/initializr/_doc/"))
				.andExpect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		this.statPublisher.handleEvent(event);
		this.mockServer.verify();
	}

	private WebProjectRequest createProjectRequest() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

	private static String mockResponse(String id, boolean created) {
		return "{\"_index\":\"initializr\",\"_type\":\"request\",\"_id\":\"" + id + "\",\"_version\":1,\"_shards\""
				+ ":{\"total\":1,\"successful\":1,\"failed\":0},\"created\":" + created + "}";
	}

	private static StatsProperties createProperties() {
		StatsProperties properties = new StatsProperties();
		Elastic elastic = properties.getElastic();
		elastic.setUri("https://example.com/elastic");
		return properties;
	}

	private static RequestMatcher json(String location, long expectedTimestamp) {
		return (request) -> {
			MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
			assertJsonContent(readJson(location), mockRequest.getBodyAsString(), expectedTimestamp);
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

	private static void assertJsonContent(String expected, String actual, long expectedTimestamp) {
		try {
			JSONAssert.assertEquals(expected, actual, new CustomComparator(JSONCompareMode.STRICT,
					Customization.customization("generationTimestamp", (o1, o2) -> (long) o1 == expectedTimestamp)));
		}
		catch (JSONException ex) {
			throw new AssertionError("Failed to parse expected or actual JSON request content", ex);
		}
	}

}
