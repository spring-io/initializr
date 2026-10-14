/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

/**
 * Thrown when a project generation failure occurs.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public class ProjectGenerationException extends RuntimeException {

	public ProjectGenerationException(String message) {
		super(message);
	}

	public ProjectGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

}
