/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.util.Locale;
import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;

/**
 * A default {@link ProjectGenerationController} that uses a standard
 * {@link ProjectRequest} to map parameters of a project generation request.
 *
 * @author Stephane Nicoll
 */
public class DefaultProjectGenerationController extends ProjectGenerationController<ProjectRequest> {

	public DefaultProjectGenerationController(InitializrMetadataProvider metadataProvider,
			ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
		super(metadataProvider, projectGenerationInvoker);
	}

	@Override
	public ProjectRequest projectRequest(Map<String, String> headers) {
		WebProjectRequest request = new WebProjectRequest();
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			request.getParameters().put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
		}
		request.initialize(getMetadata());
		return request;
	}

}
