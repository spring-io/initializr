/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
