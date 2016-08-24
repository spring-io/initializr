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

package io.spring.initializr.web.project

import io.spring.initializr.metadata.InitializrMetadata

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse

import io.spring.initializr.generator.InvalidProjectRequestException
import io.spring.initializr.metadata.InitializrMetadataProvider

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

import static io.spring.initializr.util.GroovyTemplate.template

/**
 * A base controller that uses a {@link InitializrMetadataProvider}
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
abstract class AbstractInitializrController {

	@Autowired
	protected InitializrMetadataProvider metadataProvider

	private boolean forceSsl

	@PostConstruct
	void initialize() {
		forceSsl = metadataProvider.get().configuration.env.forceSsl
	}

	@ExceptionHandler
	public void invalidProjectRequest(HttpServletResponse response, InvalidProjectRequestException ex) {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	/**
	 * Render the home page with the specified template.
	 */
	protected String renderHome(String templatePath) {
		def model = generateDefaultModel(metadataProvider.get())

		template templatePath, model
	}

	protected LinkedHashMap generateDefaultModel(InitializrMetadata metadata) {
		def model = [:]
		model['serviceUrl'] = generateAppUrl()
		metadata.properties.each {
			if (it.key.equals('types')) {
				model['types'] = it.value.clone()
			} else {
				model[it.key] = it.value
			}
		}

		// Only keep project type
		model['types'].content.removeAll { t -> !'project'.equals(t.tags['format']) }

		// Google analytics support
		model['trackingCode'] = metadata.configuration.env.googleAnalyticsTrackingCode
		model
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates.
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#forceSsl
	 */
	protected String generateAppUrl() {
		def builder = ServletUriComponentsBuilder.fromCurrentServletMapping()
		if (this.forceSsl) {
			builder.scheme('https')
		}
		builder.build()
	}

}
