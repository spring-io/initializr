/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
