/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web;

import java.nio.charset.StandardCharsets;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;
import org.jspecify.annotations.Nullable;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.RequestHeadersSpec;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;
import org.springframework.util.ObjectUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for integration tests that invoke a running server on a random port using a
 * {@link RestTestClient}.
 * <p>
 * Use only when the test needs a real server, e.g. to assert the rendered error body or
 * to fetch a resource by URL. Otherwise, use
 * {@link AbstractInitializrControllerIntegrationTests}.
 *
 * @author Stephane Nicoll
 * @author Dave Syer
 * @author Moritz Halbritter
 */
@SpringBootTest(classes = Config.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public abstract class AbstractFullStackInitializrIntegrationTests extends AbstractInitializrIntegrationTests {

	@LocalServerPort
	protected int port;

	protected String host = "localhost";

	@Autowired
	protected RestTestClient client;

	@Override
	protected String authority() {
		return this.host + ":" + this.port;
	}

	protected String createUrl(String context) {
		return "http://" + this.host + ":" + this.port + (context.startsWith("/") ? context : "/" + context);
	}

	protected void validateContentType(RestTestClientResponse response, MediaType expected) {
		assertThat(response).hasContentTypeCompatibleWith(expected);
	}

	protected void validateMetadata(RestTestClientResponse response, MediaType mediaType, String version,
			JSONCompareMode compareMode) {
		validateContentType(response, mediaType);
		validateMetadata(bodyOf(response), version, compareMode);
	}

	protected void validateDefaultMetadata(RestTestClientResponse response) {
		validateMetadata(response, DEFAULT_METADATA_MEDIA_TYPE, "2.1.0", JSONCompareMode.STRICT);
	}

	protected void validateCurrentMetadata(RestTestClientResponse response) {
		validateMetadata(response, CURRENT_METADATA_MEDIA_TYPE, "2.3.0", JSONCompareMode.STRICT);
	}

	/**
	 * Assert that the request was rejected with a {@code 400} and a message containing
	 * the specified values.
	 * @param response the response
	 * @param values the values the error message should contain
	 */
	protected void assertBadRequest(RestTestClientResponse response, String... values) {
		assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
			.bodyJson()
			.extractingPath("$.message")
			.asString()
			.contains(values);
	}

	protected ProjectStructure downloadZip(String context) {
		return projectFromArchive(downloadSuccessfully(context));
	}

	protected ProjectStructure downloadTgz(String context) {
		return tgzProjectAssert(downloadSuccessfully(context));
	}

	private byte[] downloadSuccessfully(String context) {
		RestTestClientResponse response = downloadArchive(context);
		assertThat(response).hasStatusOk();
		return response.getExchangeResult().getResponseBodyContent();
	}

	protected RestTestClientResponse downloadArchive(String context) {
		return RestTestClientResponse.from(this.client.get().uri(context).exchange());
	}

	protected RestTestClientResponse invokeHome(@Nullable String userAgentHeader, String @Nullable ... acceptHeaders) {
		return execute("/", userAgentHeader, acceptHeaders);
	}

	protected RestTestClientResponse execute(String context, @Nullable String userAgentHeader,
			String @Nullable ... acceptHeaders) {
		RequestHeadersSpec<?> request = this.client.get().uri(context);
		if (userAgentHeader != null) {
			request.header(HttpHeaders.USER_AGENT, userAgentHeader);
		}
		if (!ObjectUtils.isEmpty(acceptHeaders)) {
			request.header(HttpHeaders.ACCEPT, acceptHeaders);
		}
		return RestTestClientResponse.from(request.exchange());
	}

	/**
	 * Return the body of the specified successful response.
	 * @param response the response
	 * @return the body
	 */
	protected String bodyOf(RestTestClientResponse response) {
		assertThat(response).hasStatus2xxSuccessful();
		return new String(response.getExchangeResult().getResponseBodyContent(), StandardCharsets.UTF_8);
	}

}
