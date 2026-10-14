/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.container.docker.compose;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.container.docker.compose.ComposeFile;
import io.spring.initializr.generator.container.docker.compose.ComposeFileWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} that creates a 'compose.yaml' file through a
 * {@link ComposeFile}.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
public class ComposeProjectContributor implements ProjectContributor {

	private final ComposeFile composeFile;

	private final IndentingWriterFactory indentingWriterFactory;

	private final ComposeFileWriter composeFileWriter;

	public ComposeProjectContributor(ComposeFile composeFile, IndentingWriterFactory indentingWriterFactory) {
		this.composeFile = composeFile;
		this.indentingWriterFactory = indentingWriterFactory;
		this.composeFileWriter = new ComposeFileWriter();
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path file = Files.createFile(projectRoot.resolve("compose.yaml"));
		writeComposeFile(Files.newBufferedWriter(file));
	}

	void writeComposeFile(Writer out) throws IOException {
		try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("yaml", out)) {
			this.composeFileWriter.writeTo(writer, this.composeFile);
		}
	}

}
