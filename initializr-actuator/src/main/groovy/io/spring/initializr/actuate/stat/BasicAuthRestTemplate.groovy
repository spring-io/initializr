/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.stat

import java.nio.charset.StandardCharsets

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.InterceptingClientHttpRequestFactory
import org.springframework.util.Base64Utils
import org.springframework.web.client.RestTemplate

/**
 * A simple {@link RestTemplate} extension that automatically provides the
 * {@code Authorization} header if credentials are provided.
 * <p>
 * Largely inspired from Spring Boot's {@code TestRestTemplate}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class BasicAuthRestTemplate extends RestTemplate {

	/**
	 * Create a new instance. {@code username} and {@code password} can be
	 * {@code null} if no authentication is necessary.
	 */
	BasicAuthRestTemplate(String username, String password) {
		addAuthentication(username, password)
	}

	private void addAuthentication(String username, String password) {
		if (!username) {
			return;
		}
		List<ClientHttpRequestInterceptor> interceptors = Collections
				.<ClientHttpRequestInterceptor> singletonList(
				new BasicAuthorizationInterceptor(username, password))
		setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(),
				interceptors))
	}

	private static class BasicAuthorizationInterceptor
			implements ClientHttpRequestInterceptor {

		private final String username

		private final String password

		BasicAuthorizationInterceptor(String username, String password) {
			this.username = username;
			this.password = (password == null ? "" : password)
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
											ClientHttpRequestExecution execution) throws IOException {
			String token = Base64Utils.encodeToString(
					(this.username + ":" + this.password).getBytes(StandardCharsets.UTF_8))
			request.getHeaders().add("Authorization", "Basic " + token)
			return execution.execute(request, body)
		}

	}

}
