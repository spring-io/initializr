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

import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.MavenRepositoryCredentials;
import io.spring.initializr.generator.io.IndentingWriter;

/**
 * {@link GradleBuild} settings writer abstraction.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Jafer Khan Shamshad
 * @see GroovyDslGradleSettingsWriter
 * @see KotlinDslGradleSettingsWriter
 */
public abstract class GradleSettingsWriter {

	/**
	 * Write a {@linkplain GradleBuild settings.gradle} using the specified
	 * {@linkplain IndentingWriter writer}.
	 * @param writer the writer to use
	 * @param build the gradle build to write
	 */
	public final void writeTo(IndentingWriter writer, GradleBuild build) {
		writePluginManagement(writer, build);
		writer.println("rootProject.name = " + wrapWithQuotes(build.getSettings().getArtifact()));
	}

	private void writePluginManagement(IndentingWriter writer, GradleBuild build) {
		if (build.pluginRepositories().isEmpty()) {
			return;
		}
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
			build.pluginRepositories().items().forEach((repository) -> writeRepository(writer, repository));
			writer.println("gradlePluginPortal()");
		});
		writer.println("}");
	}

	private void writeResolutionStrategyIfNecessary(IndentingWriter writer, GradleBuild build) {
		if (build.pluginRepositories().items().allMatch(MavenRepository.MAVEN_CENTRAL::equals)) {
			return;
		}
		writer.println("resolutionStrategy {");
		writer.indented(() -> {
			writer.println("eachPlugin {");
			writer.indented(() -> {
				writer.println("if (requested.id.id == " + wrapWithQuotes("org.springframework.boot") + ") {");
				writer.indented(() -> writer.println(
						"useModule(\"org.springframework.boot:spring-boot-gradle-plugin:${requested.version}\")"));
				writer.println("}");
			});
			writer.println("}");
		});
		writer.println("}");
	}

	private void writeRepository(IndentingWriter writer, MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			writer.println("mavenCentral()");
			return;
		}

		writer.println("maven {");
		writer.indented(() -> {
			writer.println(propertyAssignment("url", repository.getUrl()));

			MavenRepositoryCredentials credentials = repository.getCredentials();
			if (credentials != null) {
				writer.println("credentials {");
				writer.indented(() -> {
					writer.println(propertyAssignment("username", credentials.getUsername()));
					writer.println(propertyAssignment("password", credentials.getPassword()));
				});
				writer.println("}");
			}
		});
		writer.println("}");
	}

	protected abstract String wrapWithQuotes(String value);

	protected abstract String propertyAssignment(String name, String value);

}
