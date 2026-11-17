/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.controller.custom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} that adds an {@code custom.txt} file at the root of the
 * project when the registered description is a {@link CustomProjectDescription} and its
 * {@code customFlag} is {@code enabled}.
 *
 * @author Stephane Nicoll
 */
class CustomProjectContributor implements ProjectContributor {

	private final ProjectDescription description;

	CustomProjectContributor(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.description instanceof CustomProjectDescription customDescription
				&& customDescription.isCustomFlag()) {
			Files.createFile(projectRoot.resolve("custom.txt"));
		}
	}

}
