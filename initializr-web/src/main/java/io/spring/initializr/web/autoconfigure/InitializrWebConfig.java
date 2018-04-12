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

package io.spring.initializr.web.autoconfigure;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.spring.initializr.util.Agent;
import io.spring.initializr.util.Agent.AgentId;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

/**
 * Spring Initializr web configuration.
 *
 * @author Stephane Nicoll
 */
public class InitializrWebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/info", "/actuator/info");
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer
				.defaultContentTypeStrategy(new CommandLineContentNegotiationStrategy());
	}

	/**
	 * A command-line aware {@link ContentNegotiationStrategy} that forces the media type
	 * to "text/plain" for compatible agents.
	 */
	private static class CommandLineContentNegotiationStrategy
			implements ContentNegotiationStrategy {

		private final UrlPathHelper urlPathHelper = new UrlPathHelper();

		@Override
		public List<MediaType> resolveMediaTypes(NativeWebRequest request)
				throws HttpMediaTypeNotAcceptableException {
			String path = this.urlPathHelper.getPathWithinApplication(
					request.getNativeRequest(HttpServletRequest.class));
			if (!StringUtils.hasText(path) || !path.equals("/")) { // Only care about "/"
				return MEDIA_TYPE_ALL_LIST;
			}
			String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
			if (userAgent != null) {
				Agent agent = Agent.fromUserAgent(userAgent);
				if (agent != null) {
					if (AgentId.CURL.equals(agent.getId())
							|| AgentId.HTTPIE.equals(agent.getId())) {
						return Collections.singletonList(MediaType.TEXT_PLAIN);
					}
				}
			}
			return Collections.singletonList(MediaType.APPLICATION_JSON);
		}

	}

}
