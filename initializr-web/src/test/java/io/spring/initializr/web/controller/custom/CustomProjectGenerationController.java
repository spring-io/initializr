/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller.custom;

import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;

/**
 * A custom {@link ProjectGenerationController} that binds request attributes to a
 * {@link CustomProjectRequest}.
 *
 * @author Stephane Nicoll
 */
class CustomProjectGenerationController extends ProjectGenerationController<CustomProjectRequest> {

	CustomProjectGenerationController(InitializrMetadataProvider metadataProvider,
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
