/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
