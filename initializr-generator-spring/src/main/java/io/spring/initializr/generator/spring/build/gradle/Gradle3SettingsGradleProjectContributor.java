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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} for the project's {@code settings.gradle} file when using
 * Gradle 3.
 *
 * @author Andy Wilkinson
 */
class Gradle3SettingsGradleProjectContributor implements ProjectContributor {

	private final GradleBuild build;

	Gradle3SettingsGradleProjectContributor(GradleBuild build) {
		this.build = build;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path file = Files.createFile(projectRoot.resolve("settings.gradle"));
		try (PrintWriter writer = new PrintWriter(Files.newOutputStream(file))) {
			writer.println("rootProject.name = '" + this.build.getArtifact() + "'");
		}
	}

}
