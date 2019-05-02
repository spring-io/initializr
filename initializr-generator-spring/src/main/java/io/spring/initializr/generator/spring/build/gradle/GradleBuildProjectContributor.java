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

package io.spring.initializr.generator.spring.build.gradle;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} for the project's main build file.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
public class GradleBuildProjectContributor implements BuildWriter, ProjectContributor {

	private final GradleBuildWriter buildWriter;

	private final GradleBuild build;

	private final IndentingWriterFactory indentingWriterFactory;

	private final String buildFileName;

	GradleBuildProjectContributor(GradleBuildWriter buildWriter, GradleBuild build,
			IndentingWriterFactory indentingWriterFactory, String buildFileName) {
		this.buildWriter = buildWriter;
		this.build = build;
		this.indentingWriterFactory = indentingWriterFactory;
		this.buildFileName = buildFileName;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path buildGradle = Files.createFile(projectRoot.resolve(this.buildFileName));
		writeBuild(Files.newBufferedWriter(buildGradle));
	}

	@Override
	public void writeBuild(Writer out) throws IOException {
		try (IndentingWriter writer = this.indentingWriterFactory
				.createIndentingWriter("gradle", out)) {
			this.buildWriter.writeTo(writer, this.build);
		}
	}

}
