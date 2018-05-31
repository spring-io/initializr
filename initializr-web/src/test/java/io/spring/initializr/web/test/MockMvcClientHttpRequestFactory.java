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

package io.spring.initializr.web.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/**
 * @author Dave Syer
 */
public class MockMvcClientHttpRequestFactory implements ClientHttpRequestFactory {

	private final MockMvc mockMvc;

	private String label = "UNKNOWN";

	private List<String> fields = new ArrayList<>();

	public MockMvcClientHttpRequestFactory(MockMvc mockMvc) {
		Assert.notNull(mockMvc, "MockMvc must not be null");
		this.mockMvc = mockMvc;
	}

	@Override
	public ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod)
			throws IOException {
		return new MockClientHttpRequest(httpMethod, uri) {
			@Override
			public ClientHttpResponse executeInternal() throws IOException {
				try {
					MockHttpServletRequestBuilder requestBuilder = request(httpMethod,
							uri.toString());
					requestBuilder.content(getBodyAsBytes());
					requestBuilder.headers(getHeaders());
					MockHttpServletResponse servletResponse = actions(requestBuilder)
							.andReturn().getResponse();
					HttpStatus status = HttpStatus.valueOf(servletResponse.getStatus());
					if (status.value() >= 400) {
						requestBuilder = request(HttpMethod.GET, "/error")
								.requestAttr(RequestDispatcher.ERROR_STATUS_CODE,
										status.value())
								.requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
										uri.toString());
						if (servletResponse.getErrorMessage() != null) {
							requestBuilder.requestAttr(RequestDispatcher.ERROR_MESSAGE,
									servletResponse.getErrorMessage());
						}
						// Overwrites the snippets from the first request
						servletResponse = actions(requestBuilder).andReturn()
								.getResponse();
					}
					byte[] body = servletResponse.getContentAsByteArray();
					HttpHeaders headers = getResponseHeaders(servletResponse);
					MockClientHttpResponse clientResponse = new MockClientHttpResponse(
							body, status);
					clientResponse.getHeaders().putAll(headers);
					return clientResponse;
				}
				catch (Exception ex) {
					throw new IllegalStateException(ex);
				}
			}

		};
	}

	private ResultActions actions(MockHttpServletRequestBuilder requestBuilder)
			throws Exception {
		ResultActions actions = MockMvcClientHttpRequestFactory.this.mockMvc
				.perform(requestBuilder);
		List<Snippet> snippets = new ArrayList<>();
		for (String field : this.fields) {
			snippets.add(new ResponseFieldSnippet(field));
		}
		actions.andDo(document(this.label, preprocessResponse(prettyPrint()),
				snippets.toArray(new Snippet[0])));
		this.fields = new ArrayList<>();
		return actions;
	}

	private HttpHeaders getResponseHeaders(MockHttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		for (String name : response.getHeaderNames()) {
			List<String> values = response.getHeaders(name);
			for (String value : values) {
				headers.add(name, value);
			}
		}
		return headers;
	}

	public void setTest(Class<?> testClass, Method testMethod) {
		this.label = testMethod.getName();
	}

	public void setFields(String... fields) {
		this.fields = Arrays.asList(fields);
	}

}
