/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
