/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.metadata.InitializrMetadata;
import org.jspecify.annotations.Nullable;

/**
 * Event published when an error occurred trying to generate a project.
 *
 * @author Stephane Nicoll
 */
public class ProjectFailedEvent extends ProjectRequestEvent {

	private final @Nullable Exception cause;

	public ProjectFailedEvent(ProjectRequest request, InitializrMetadata metadata, @Nullable Exception cause) {
		super(request, metadata);
		this.cause = cause;
	}

	/**
	 * Return the cause of the failure.
	 * @return the cause of the failure
	 */
	public @Nullable Exception getCause() {
		return this.cause;
	}

}
