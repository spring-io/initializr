/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

/**
 * A factory for {@link ProjectDescriptionDiff}.
 *
 * @author Chris Bono
 */
public interface ProjectDescriptionDiffFactory {

	/**
	 * Create a {@link ProjectDescriptionDiff} for the specified
	 * {@link ProjectDescription}. Any change on the specified {@code description} is
	 * tracked by the returned instance.
	 * @param description the project description to use as the source of the diff
	 * @return a diff instance using the current state of the specified description as its
	 * source
	 */
	ProjectDescriptionDiff create(ProjectDescription description);

}
