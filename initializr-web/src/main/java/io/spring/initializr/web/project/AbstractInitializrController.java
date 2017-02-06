/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.web.project;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.spring.initializr.generator.InvalidProjectRequestException;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.TypeCapability;
import io.spring.initializr.util.GroovyTemplate;

/**
 * A base controller that uses a {@link InitializrMetadataProvider}
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractInitializrController {

	protected final InitializrMetadataProvider metadataProvider;
	private final GroovyTemplate groovyTemplate;
	private final Function<String, String> linkTo;
	private Boolean forceSsl;

	protected AbstractInitializrController(InitializrMetadataProvider metadataProvider,
			ResourceUrlProvider resourceUrlProvider, GroovyTemplate groovyTemplate) {
		this.metadataProvider = metadataProvider;
		this.groovyTemplate = groovyTemplate;
		this.linkTo = link -> {
			String result = resourceUrlProvider.getForLookupPath(link);
			return result == null ? link : result;
		};
	}

	public boolean isForceSsl() {
		if (this.forceSsl == null) {
			this.forceSsl = metadataProvider.get().getConfiguration().getEnv()
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
	 * Render the home page with the specified template.
	 */
	protected String renderHome(String templatePath) throws Exception {
		InitializrMetadata metadata = metadataProvider.get();

		Map<String,Object> model = new LinkedHashMap<>();
		model.put("serviceUrl", generateAppUrl());
		BeanWrapperImpl wrapper = new BeanWrapperImpl(metadata);
		for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
			if ("types".equals(descriptor.getName())) {
				model.put("types", removeTypes(metadata.getTypes()));
			} else {
				model.put(descriptor.getName(), wrapper.getPropertyValue(descriptor.getName()));
			}
		}

		// Google analytics support
		model.put("trackingCode",
				metadata.getConfiguration().getEnv().getGoogleAnalyticsTrackingCode());

		// Linking to static resources
		model.put("linkTo", this.linkTo);

		return groovyTemplate.process(templatePath, model);
	}

	private TypeCapability removeTypes(TypeCapability types) {
		TypeCapability result = new TypeCapability();
		result.setDescription(types.getDescription());
		result.setTitle(types.getTitle());
		result.getContent().addAll(types.getContent());
		// Only keep project type
		result.getContent().removeIf(t -> !"project".equals(t.getTags().get("format")) );
		return result;
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates.
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#forceSsl
	 */
	protected String generateAppUrl() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		if (isForceSsl()) {
			builder.scheme("https");
		}
		return builder.build().toString();
	}

}
