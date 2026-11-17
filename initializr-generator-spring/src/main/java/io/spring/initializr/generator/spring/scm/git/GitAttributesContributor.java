/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.scm.git;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} that contributes a {@code .gitattributes} file to a
 * project.
 *
 * @author Moritz Halbritter
 */
public class GitAttributesContributor implements ProjectContributor {

	private final GitAttributes gitAttributes;

	public GitAttributesContributor(GitAttributes gitAttributes) {
		this.gitAttributes = gitAttributes;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.gitAttributes.isEmpty()) {
			return;
		}
		Path file = Files.createFile(projectRoot.resolve(".gitattributes"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			this.gitAttributes.write(writer);
		}
	}

}
