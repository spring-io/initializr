/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.doc.generator.project;

import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;

/**
 * Example of a custom {@link ProjectGenerationController}.
 *
 * @author Stephane Nicoll
 */
// tag::code[]
public class CustomProjectGenerationController extends ProjectGenerationController<CustomProjectRequest> {

	public CustomProjectGenerationController(InitializrMetadataProvider metadataProvider,
			ProjectGenerationInvoker<CustomProjectRequest> projectGenerationInvoker) {
		super(metadataProvider, projectGenerationInvoker);
	}

	@Override
	public CustomProjectRequest projectRequest(Map<String, String> headers) {
		CustomProjectRequest request = new CustomProjectRequest();
		request.getParameters().putAll(headers);
		request.initialize(getMetadata());
		return request;
	}

}
// end::code[]
