/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

/**
 * Thrown when the configuration defines invalid metadata.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public class InvalidInitializrMetadataException extends RuntimeException {

	public InvalidInitializrMetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInitializrMetadataException(String message) {
		super(message);
	}

}
