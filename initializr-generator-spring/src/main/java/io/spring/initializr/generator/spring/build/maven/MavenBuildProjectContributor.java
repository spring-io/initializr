/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.spring.build.maven;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} to contribute the files for a {@link MavenBuild}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MavenBuildProjectContributor implements BuildWriter, ProjectContributor {

	private final MavenBuild build;

	private final IndentingWriterFactory indentingWriterFactory;

	private final MavenBuildWriter buildWriter;

	public MavenBuildProjectContributor(MavenBuild build,
			IndentingWriterFactory indentingWriterFactory) {
		this.build = build;
		this.indentingWriterFactory = indentingWriterFactory;
		this.buildWriter = new MavenBuildWriter();
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path pomFile = Files.createFile(projectRoot.resolve("pom.xml"));
		writeBuild(Files.newBufferedWriter(pomFile));
	}

	@Override
	public void writeBuild(Writer out) throws IOException {
		try (IndentingWriter writer = this.indentingWriterFactory
				.createIndentingWriter("maven", out)) {
			this.buildWriter.writeTo(writer, this.build);
		}
	}

}
