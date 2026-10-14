/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.generator.project.ProjectGenerationException;

/**
 * Thrown when a {@link ProjectRequest} is invalid.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public class InvalidProjectRequestException extends ProjectGenerationException {

	public InvalidProjectRequestException(String message) {
		super(message);
	}

}
