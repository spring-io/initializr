/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Event published when a {@link ProjectRequest} has been processed.
 *
 * @author Stephane Nicoll
 * @see ProjectGeneratedEvent
 * @see ProjectFailedEvent
 */
public abstract class ProjectRequestEvent {

	private final ProjectRequest request;

	private final InitializrMetadata metadata;

	private final long timestamp;

	protected ProjectRequestEvent(ProjectRequest request, InitializrMetadata metadata) {
		this.request = request;
		this.metadata = metadata;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Return the {@link ProjectRequest} used to generate the project.
	 * @return the project request
	 */
	public ProjectRequest getProjectRequest() {
		return this.request;
	}

	/**
	 * Return the timestamp at which the request was processed.
	 * @return the timestamp that the request was processed
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Return the metadata that was used to generate the project.
	 * @return the metadata
	 */
	public InitializrMetadata getMetadata() {
		return this.metadata;
	}

}
