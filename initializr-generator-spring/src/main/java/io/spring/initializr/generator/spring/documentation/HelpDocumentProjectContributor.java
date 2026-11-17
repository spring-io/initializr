/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.documentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} for the project's {@code HELP.md} file.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
public class HelpDocumentProjectContributor implements ProjectContributor {

	private final HelpDocument helpDocument;

	public HelpDocumentProjectContributor(HelpDocument helpDocument) {
		this.helpDocument = helpDocument;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.helpDocument.isEmpty()) {
			return;
		}
		Path file = Files.createFile(projectRoot.resolve("HELP.md"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			this.helpDocument.write(writer);
		}
	}

}
