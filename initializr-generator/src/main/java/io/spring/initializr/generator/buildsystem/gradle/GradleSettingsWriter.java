/*
 * Copyright 2012-2021 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSettings.PluginMapping;
import io.spring.initializr.generator.io.IndentingWriter;

/**
 * {@link GradleBuild} settings writer abstraction.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
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
		if (build.pluginRepositories().isEmpty() && build.getSettings().getPluginMappings().isEmpty()) {
			return;
		}
		writer.println("pluginManagement {");
		writer.indented(() -> {
			writeRepositories(writer, build);
			writeResolutionStrategy(writer, build);
		});
		writer.println("}");
	}

	private void writeRepositories(IndentingWriter writer, GradleBuild build) {
		if (build.pluginRepositories().isEmpty()) {
			return;
		}
		writer.println("repositories {");
		writer.indented(() -> {
			build.pluginRepositories().items().map(this::repositoryAsString).forEach(writer::println);
			writer.println("gradlePluginPortal()");
		});
		writer.println("}");
	}

	private void writeResolutionStrategy(IndentingWriter writer, GradleBuild build) {
		if (build.getSettings().getPluginMappings().isEmpty()) {
			return;
		}
		writer.println("resolutionStrategy {");
		writer.indented(() -> {
			writer.println("eachPlugin {");
			writer.indented(() -> build.getSettings().getPluginMappings()
					.forEach((pluginMapping) -> writePluginMapping(writer, pluginMapping)));
			writer.println("}");
		});
		writer.println("}");
	}

	private void writePluginMapping(IndentingWriter writer, PluginMapping pluginMapping) {
		writer.println("if (requested.id.id == " + wrapWithQuotes(pluginMapping.getId()) + ") {");
		Dependency dependency = pluginMapping.getDependency();
		String module = String.format("%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(),
				dependency.getVersion().getValue());
		writer.indented(() -> writer.println("useModule(" + wrapWithQuotes(module) + ")"));
		writer.println("}");
	}

	private String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { " + urlAssignment(repository.getUrl()) + " }";
	}

	protected abstract String wrapWithQuotes(String value);

	protected abstract String urlAssignment(String url);

}
