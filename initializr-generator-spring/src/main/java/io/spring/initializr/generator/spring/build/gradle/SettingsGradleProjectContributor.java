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
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleSettingsWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} for the project's settings file.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
class SettingsGradleProjectContributor implements ProjectContributor {

	private final GradleBuild build;

	private final IndentingWriterFactory indentingWriterFactory;

	private final GradleSettingsWriter settingsWriter;

	private final String settingsFileName;

	SettingsGradleProjectContributor(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory,
			GradleSettingsWriter settingsWriter, String settingsFileName) {
		this.build = build;
		this.indentingWriterFactory = indentingWriterFactory;
		this.settingsWriter = settingsWriter;
		this.settingsFileName = settingsFileName;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path settingsGradle = Files
				.createFile(projectRoot.resolve(this.settingsFileName));
		try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter(
				"gradle", Files.newBufferedWriter(settingsGradle))) {
			this.settingsWriter.writeTo(writer, this.build);
		}
	}

}
