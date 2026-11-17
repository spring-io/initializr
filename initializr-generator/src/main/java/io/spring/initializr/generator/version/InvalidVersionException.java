/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
