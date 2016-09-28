/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.RequestDispatcher;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * @author Dave Syer
 *
 */
public class MockMvcClientHttpRequestFactory implements ClientHttpRequestFactory {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private final MockMvc mockMvc;

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
					String label = label(httpMethod, uri.toString(), getHeaders());
					MvcResult mvcResult = MockMvcClientHttpRequestFactory.this.mockMvc
							.perform(requestBuilder)
							.andDo(document(label, preprocessResponse(prettyPrint())))
							.andReturn();
					MockHttpServletResponse servletResponse = mvcResult.getResponse();
					HttpStatus status = HttpStatus.valueOf(servletResponse.getStatus());
					if (status.value() >= 400) {
						MockHttpServletRequestBuilder request = request(HttpMethod.GET,
								"/error")
										.requestAttr(RequestDispatcher.ERROR_STATUS_CODE,
												status.value())
										.requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
												uri.toString());
						if (servletResponse.getErrorMessage() != null) {
							request.requestAttr(RequestDispatcher.ERROR_MESSAGE,
									servletResponse.getErrorMessage());
						}
						// Overwrites the snippets from the first request
						mvcResult = MockMvcClientHttpRequestFactory.this.mockMvc
								.perform(request)
								.andDo(document(label, preprocessResponse(prettyPrint())))
								.andReturn();
						servletResponse = mvcResult.getResponse();
					}
					byte[] body = servletResponse.getContentAsByteArray();
					HttpHeaders headers = getResponseHeaders(servletResponse);
					MockClientHttpResponse clientResponse = new MockClientHttpResponse(
							body, status);
					clientResponse.getHeaders().putAll(headers);
					return clientResponse;
				}
				catch (Exception ex) {
					byte[] body = ex.toString().getBytes(UTF8_CHARSET);
					return new MockClientHttpResponse(body,
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		};
	}

	private String label(HttpMethod method, String path, HttpHeaders headers) {
		StringBuilder label = new StringBuilder();
		String query = null;
		if (path.contains("?")) {
			query = path.substring(path.indexOf("?") + 1);
			path = path.substring(0, path.indexOf("?"));
		}
		UriComponents uri = UriComponentsBuilder.fromPath(path).query(query).build();
		if (method != null) {
			label.append(method.toString().toLowerCase());
		}
		if ("/".equals(uri.getPath())) {
			label.append("/ROOT");
		}
		else {
			label.append(uri.getPath());
		}
		if (query != null) {
			label.append("/queries");
			MultiValueMap<String, String> params = uri.getQueryParams();
			for (String name : params.keySet()) {
				label.append("/").append(name).append("-").append(params.getFirst(name));
			}
		}
		if (headers != null && !headers.isEmpty()) {
			label.append("/headers");
			for (String name : headers.keySet()) {
				if (name.equals("Content-Length")) {
					continue;
				}
				String value = headers.getFirst(name);
				if (value.contains("*")) {
					value = value.replace("*", "ALL");
				}
				if (value.contains("/")) {
					value = value.replace("/", "_");
				}
				if (value.contains(", ")) {
					value = value.substring(0, value.indexOf(", ")) + ".MORE";
				}
				if (!StringUtils.hasText(value)) {
					value = "EMPTY";
				}
				label.append("/").append(name);
				label.append("-").append(value);
			}
		}
		return label.toString();
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

}