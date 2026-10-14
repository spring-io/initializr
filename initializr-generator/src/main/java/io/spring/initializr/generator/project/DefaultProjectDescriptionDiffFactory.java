/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

/**
 * A default {@link ProjectDescriptionDiffFactory} implementation that creates default
 * {@link ProjectDescriptionDiff} instances.
 *
 * @author Chris Bono
 */
public class DefaultProjectDescriptionDiffFactory implements ProjectDescriptionDiffFactory {

	@Override
	public ProjectDescriptionDiff create(ProjectDescription description) {
		return new ProjectDescriptionDiff(description);
	}

}
