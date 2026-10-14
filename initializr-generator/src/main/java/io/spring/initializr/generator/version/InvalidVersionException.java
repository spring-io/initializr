/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.version;

/**
 * Thrown if a input represents an invalid version.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public class InvalidVersionException extends RuntimeException {

	/**
	 * Creates a new instance.
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance.
	 * @param message the message
	 */
	public InvalidVersionException(String message) {
		super(message);
	}

}
