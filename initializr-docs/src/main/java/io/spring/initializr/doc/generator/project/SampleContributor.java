/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.doc.generator.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A sample {@link ProjectContributor} that creates a {@code hello.txt} at the root of the
 * project directory with content {@code Test}.
 *
 * @author Stephane Nicoll
 */
// tag::code[]
public class SampleContributor implements ProjectContributor {

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path file = Files.createFile(projectRoot.resolve("hello.txt"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			writer.println("Test");
		}
	}

}
// end::code[]
