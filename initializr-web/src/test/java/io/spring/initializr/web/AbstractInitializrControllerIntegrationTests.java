/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests.MockMvcConfig;
import org.jspecify.annotations.Nullable;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.util.ObjectUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Base class for integration tests that invoke the web layer using {@link MockMvcTester},
 * without starting a server. Requests target {@code start.example.com} unless a
 * {@code Host} header is set. Error responses expose the status and error message, but
 * not the error body rendered by the server.
 * <p>
 * Prefer this class over {@link AbstractFullStackInitializrIntegrationTests}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
@ContextConfiguration(classes = MockMvcConfig.class)
@AutoConfigureMockMvc
public abstract class AbstractInitializrControllerIntegrationTests extends AbstractInitializrIntegrationTests {

	private static final String HOST = "start.example.com";

	@Autowired
	protected MockMvcTester mvc;

	@Override
	protected String authority() {
		return HOST;
	}

	protected void validateContentType(MvcTestResult result, MediaType expected) {
		assertThat(result).hasContentTypeCompatibleWith(expected);
	}

	protected void validateMetadata(MvcTestResult result, MediaType mediaType, String version,
			JSONCompareMode compareMode) {
		validateContentType(result, mediaType);
		validateMetadata(bodyOf(result), version, compareMode);
	}

	protected void validateDefaultMetadata(MvcTestResult result) {
		validateMetadata(result, DEFAULT_METADATA_MEDIA_TYPE, "2.1.0", JSONCompareMode.STRICT);
	}

	protected void validateCurrentMetadata(MvcTestResult result) {
		validateMetadata(result, CURRENT_METADATA_MEDIA_TYPE, "2.3.0", JSONCompareMode.STRICT);
	}

	/**
	 * Assert that the request was rejected with a {@code 400} and a message containing
	 * the specified values.
	 * @param result the result
	 * @param values the values the error message should contain
	 */
	protected void assertBadRequest(MvcTestResult result, String... values) {
		assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
		assertThat(result.getResponse().getErrorMessage()).contains(values);
	}

	protected ProjectStructure downloadZip(String context) {
		return projectFromArchive(downloadSuccessfully(context));
	}

	protected ProjectStructure downloadTgz(String context) {
		return tgzProjectAssert(downloadSuccessfully(context));
	}

	private byte[] downloadSuccessfully(String context) {
		MvcTestResult result = downloadArchive(context);
		assertThat(result).hasStatusOk();
		return result.getResponse().getContentAsByteArray();
	}

	protected MvcTestResult downloadArchive(String context) {
		return this.mvc.get().uri(context).exchange();
	}

	protected MvcTestResult invokeHome(@Nullable String userAgentHeader, String @Nullable ... acceptHeaders) {
		return execute("/", userAgentHeader, acceptHeaders);
	}

	protected MvcTestResult execute(String context, @Nullable String userAgentHeader,
			String @Nullable ... acceptHeaders) {
		MockMvcRequestBuilder request = this.mvc.get().uri(context);
		if (userAgentHeader != null) {
			request.header(HttpHeaders.USER_AGENT, userAgentHeader);
		}
		if (!ObjectUtils.isEmpty(acceptHeaders)) {
			request.accept(acceptHeaders);
		}
		return request.exchange();
	}

	/**
	 * Return the body of the specified successful result.
	 * @param result the result
	 * @return the body
	 */
	protected String bodyOf(MvcTestResult result) {
		assertThat(result).hasStatus2xxSuccessful();
		try {
			return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Cannot read response body", ex);
		}
	}

	@Configuration
	static class MockMvcConfig {

		// Requests without an explicit 'Host' header target the public host, e.g.
		// links in metadata use 'http://start.example.com/...'
		@Bean
		MockMvcBuilderCustomizer defaultHostCustomizer() {
			return (builder) -> builder.defaultRequest(get("/").header(HttpHeaders.HOST, HOST));
		}

	}

}
