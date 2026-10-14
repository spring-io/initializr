/*
 * Copyright 2012 - present the original author or authors.
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
