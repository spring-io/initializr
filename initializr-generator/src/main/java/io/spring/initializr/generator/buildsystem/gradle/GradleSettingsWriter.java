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

package io.spring.initializr.generator.buildsystem.gradle;

import java.io.IOException;

import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;

/**
 * A {@link GradleBuild} writer for {@code settings.gradle}.
 *
 * @author Andy Wilkinson
 */
public class GradleSettingsWriter {

	public void writeTo(IndentingWriter writer, GradleBuild build) throws IOException {
		writePluginManagement(writer, build);
		writer.println("rootProject.name = '" + build.getArtifact() + "'");
	}

	private void writePluginManagement(IndentingWriter writer, GradleBuild build) {
		writer.println("pluginManagement {");
		writer.indented(() -> {
			writeRepositories(writer, build);
			writeResolutionStrategyIfNecessary(writer, build);
		});
		writer.println("}");
	}

	private void writeRepositories(IndentingWriter writer, GradleBuild build) {
		writer.println("repositories {");
		writer.indented(() -> {
			build.pluginRepositories().items().map(this::repositoryAsString)
					.forEach(writer::println);
			writer.println("gradlePluginPortal()");
		});
		writer.println("}");
	}

	private void writeResolutionStrategyIfNecessary(IndentingWriter writer,
			GradleBuild build) {
		if (build.pluginRepositories().items()
				.allMatch(MavenRepository.MAVEN_CENTRAL::equals)) {
			return;
		}
		writer.println("resolutionStrategy {");
		writer.indented(() -> {
			writer.println("eachPlugin {");
			writer.indented(() -> {
				writer.println("if (requested.id.id == 'org.springframework.boot') {");
				writer.indented(() -> writer.println(
						"useModule(\"org.springframework.boot:spring-boot-gradle-plugin:${requested.version}\")"));
				writer.println("}");
			});
			writer.println("}");
		});
		writer.println("}");
	}

	private String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { url '" + repository.getUrl() + "' }";
	}

}
