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

package io.spring.initializr.web

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.accept.ContentNegotiationStrategy
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.util.UrlPathHelper

import javax.servlet.http.HttpServletRequest

/**
 * Spring Initializr web configuration.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class WebConfig extends WebMvcConfigurerAdapter {

	static final String CURL_USER_AGENT_PREFIX = 'curl'

	static final String HTTPIE_USER_AGENT_PREFIX = 'HTTPie'

	static final String SPRING_BOOT_CLI_AGENT_PREFIX = 'SpringBootCli'

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentTypeStrategy(new CommandLineContentNegotiationStrategy())
	}

	/**
	 * A command-line aware {@link ContentNegotiationStrategy} that forces the media type
	 * to "text/plain" for compatible agents.
	 */
	private static class CommandLineContentNegotiationStrategy implements ContentNegotiationStrategy {

		private final UrlPathHelper urlPathHelper = new UrlPathHelper();

		@Override
		List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
			String path = urlPathHelper.getPathWithinApplication(
					request.getNativeRequest(HttpServletRequest.class));
			if (!path || !path.equals("/")) {  // Only care about "/"
				return Collections.emptyList()
			}
			String userAgent = request.getHeader(HttpHeaders.USER_AGENT)
			if (userAgent) {
				if (userAgent.startsWith(CURL_USER_AGENT_PREFIX) || userAgent.startsWith(HTTPIE_USER_AGENT_PREFIX)) {
					return Collections.singletonList(MediaType.TEXT_PLAIN)
				}
			}
			return Collections.singletonList(MediaType.APPLICATION_JSON)
		}
	}

}

