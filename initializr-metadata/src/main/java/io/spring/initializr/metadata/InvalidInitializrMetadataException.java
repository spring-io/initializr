/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
