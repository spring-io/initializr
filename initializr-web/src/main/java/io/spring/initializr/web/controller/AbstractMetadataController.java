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

package io.spring.initializr.web.controller;

import java.nio.charset.StandardCharsets;

import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * A base controller that uses a {@link InitializrMetadataProvider}.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractMetadataController {

	protected final InitializrMetadataProvider metadataProvider;

	private Boolean forceSsl;

	protected AbstractMetadataController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates.
	 * @return the app URL
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#isForceSsl()
	 */
	protected String generateAppUrl() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		if (isForceSsl()) {
			builder.scheme("https");
		}
		return builder.build().toString();
	}

	protected String createUniqueId(String content) {
		StringBuilder builder = new StringBuilder();
		DigestUtils.appendMd5DigestAsHex(content.getBytes(StandardCharsets.UTF_8), builder);
		return builder.toString();
	}

	private boolean isForceSsl() {
		if (this.forceSsl == null) {
			this.forceSsl = this.metadataProvider.get().getConfiguration().getEnv().isForceSsl();
		}
		return this.forceSsl;

	}

}
