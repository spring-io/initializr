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
 * A {@link ProjectContributor} that contributes a {@code .gitignore} file to a project.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class GitIgnoreContributor implements ProjectContributor {

	private final GitIgnore gitIgnore;

	public GitIgnoreContributor(GitIgnore gitIgnore) {
		this.gitIgnore = gitIgnore;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.gitIgnore.isEmpty()) {
			return;
		}
		Path file = Files.createFile(projectRoot.resolve(".gitignore"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			this.gitIgnore.write(writer);
		}
	}

}
