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

package io.spring.initializr.web.project;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * A base controller that uses a {@link InitializrMetadataProvider}.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractInitializrController {

	protected final InitializrMetadataProvider metadataProvider;

	private Boolean forceSsl;

	protected AbstractInitializrController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	public boolean isForceSsl() {
		if (this.forceSsl == null) {
			this.forceSsl = this.metadataProvider.get().getConfiguration().getEnv()
					.isForceSsl();
		}
		return this.forceSsl;

	}

	@ExceptionHandler
	public void invalidProjectRequest(HttpServletResponse response,
			InvalidProjectRequestException ex) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates.
	 * @return the app URL
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#isForceSsl()
	 */
	protected String generateAppUrl() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
				.fromCurrentServletMapping();
		if (isForceSsl()) {
			builder.scheme("https");
		}
		return builder.build().toString();
	}

}
