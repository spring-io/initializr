/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Event published when a new project has been generated successfully.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratedEvent extends ProjectRequestEvent {

	public ProjectGeneratedEvent(ProjectRequest request, InitializrMetadata metadata) {
		super(request, metadata);
	}

}
