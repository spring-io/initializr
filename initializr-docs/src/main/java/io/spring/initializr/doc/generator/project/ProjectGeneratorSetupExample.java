/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.project.ProjectGenerator;

import org.springframework.context.ApplicationContext;

/**
 * Setup a {@link ProjectGenerator} with a parent context and a custom contributor.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratorSetupExample {

	// tag::code[]
	public ProjectGenerator createProjectGenerator(ApplicationContext appContext) {
		return new ProjectGenerator((context) -> {
			context.setParent(appContext);
			context.registerBean(SampleContributor.class, SampleContributor::new);
		});
	}
	// end::code[]

}
