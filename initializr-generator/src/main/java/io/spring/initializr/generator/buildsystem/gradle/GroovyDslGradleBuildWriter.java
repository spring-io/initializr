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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link GradleBuild} writer for {@code build.gradle}.
 *
 * @author Jean-Baptiste Nizet
 */
public class GroovyDslGradleBuildWriter extends GradleBuildWriter {

	@Override
	protected void writeBuildscript(IndentingWriter writer, GradleBuild build) {
		List<String> dependencies = build.getBuildscript().getDependencies();
		Map<String, String> ext = build.getBuildscript().getExt();
		if (dependencies.isEmpty() && ext.isEmpty()) {
			return;
		}
		writer.println("buildscript {");
		writer.indented(() -> {
			writeBuildscriptExt(writer, build);
			writeBuildscriptRepositories(writer, build);
			writeBuildscriptDependencies(writer, build);
		});
		writer.println("}");
		writer.println();
	}

	private void writeBuildscriptExt(IndentingWriter writer, GradleBuild build) {
		writeNestedMap(writer, "ext", build.getBuildscript().getExt(), (key, value) -> key + " = " + value);
	}

	private void writeBuildscriptRepositories(IndentingWriter writer, GradleBuild build) {
		writeRepositories(writer, build);
	}

	private void writeBuildscriptDependencies(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "dependencies", build.getBuildscript().getDependencies(),
				(dependency) -> "classpath '" + dependency + "'");
	}

	@Override
	protected void writePlugins(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "plugins", extractStandardPlugin(build), this::pluginAsString);
		writeCollection(writer, extractApplyPlugins(build), (plugin) -> "apply plugin: '" + plugin.getId() + "'",
				writer::println);
		writer.println();
	}

	private List<GradlePlugin> extractApplyPlugins(GradleBuild build) {
		return build.plugins().values().filter(GradlePlugin::isApply).collect(Collectors.toList());
	}

	private String pluginAsString(StandardGradlePlugin plugin) {
		String string = "id '" + plugin.getId() + "'";
		if (plugin.getVersion() != null) {
			string += " version '" + plugin.getVersion() + "'";
		}
		return string;
	}

	@Override
	protected void writeJavaSourceCompatibility(IndentingWriter writer, GradleBuildSettings settings) {
		writeProperty(writer, "sourceCompatibility", settings.getSourceCompatibility());
	}

	@Override
	protected String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { url '" + repository.getUrl() + "' }";
	}

	@Override
	protected void writeExtraProperties(IndentingWriter writer, Map<String, String> allProperties) {
		writeNestedCollection(writer, "ext", allProperties.entrySet(),
				(e) -> getFormattedExtraProperty(e.getKey(), e.getValue()), writer::println);
	}

	private String getFormattedExtraProperty(String key, String value) {
		return String.format("set('%s', %s)", key, value);
	}

	@Override
	protected void writeConfigurations(IndentingWriter writer, GradleConfigurationContainer configurations) {
		if (configurations.isEmpty()) {
			return;
		}
		writer.println("configurations {");
		writer.indented(() -> {
			configurations.names().forEach(writer::println);
			configurations.customizations().forEach((configuration) -> writeConfiguration(writer, configuration));
		});
		writer.println("}");
		writer.println("");
	}

	protected void writeConfiguration(IndentingWriter writer, GradleConfiguration configuration) {
		writer.println(configuration.getName() + " {");
		writer.indented(() -> writer
			.println(String.format("extendsFrom %s", String.join(", ", configuration.getExtendsFrom()))));
		writer.println("}");
	}

	@Override
	protected void writeDependency(IndentingWriter writer, Dependency dependency) {
		String quoteStyle = determineQuoteStyle(dependency.getVersion());
		String version = determineVersion(dependency.getVersion());
		String classifier = dependency.getClassifier();
		String type = dependency.getType();
		boolean hasExclusions = !dependency.getExclusions().isEmpty();
		writer.print(configurationForDependency(dependency));
		writer.print((hasExclusions) ? "(" : " ");
		writer.print(quoteStyle + dependency.getGroupId() + ":" + dependency.getArtifactId()
				+ ((version != null) ? ":" + version : "") + ((classifier != null) ? ":" + classifier : "")
				+ ((type != null) ? "@" + type : "") + quoteStyle);
		if (hasExclusions) {
			writer.println(") {");
			writer
				.indented(() -> writeCollection(writer, dependency.getExclusions(), this::dependencyExclusionAsString));
			writer.println("}");
		}
		else {
			writer.println();
		}
	}

	private String dependencyExclusionAsString(Exclusion exclusion) {
		return "exclude group: '" + exclusion.getGroupId() + "', module: '" + exclusion.getArtifactId() + "'";
	}

	private String determineQuoteStyle(VersionReference versionReference) {
		return (versionReference != null && versionReference.isProperty()) ? "\"" : "'";
	}

	@Override
	protected String bomAsString(BillOfMaterials bom) {
		String quoteStyle = determineQuoteStyle(bom.getVersion());
		return "mavenBom " + quoteStyle + bom.getGroupId() + ":" + bom.getArtifactId() + ":"
				+ determineVersion(bom.getVersion()) + quoteStyle;
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference != null) {
			if (versionReference.isProperty()) {
				VersionProperty property = versionReference.getProperty();
				return "${" + (property.isInternal() ? property.toCamelCaseFormat()
						: ("property('" + property.toStandardFormat() + "')")) + "}";
			}
			return versionReference.getValue();
		}
		return null;
	}

	@Override
	protected void writeTasks(IndentingWriter writer, GradleTaskContainer tasks) {
		tasks.values().filter((candidate) -> candidate.getType() != null).forEach((task) -> {
			writer.println();
			writer.println("tasks.withType(" + task.getName() + ") {");
			writer.indented(() -> writeTaskCustomization(writer, task));
			writer.println("}");
		});
		tasks.values().filter((candidate) -> candidate.getType() == null).forEach((task) -> {
			writer.println();
			writer.println("tasks.named('" + task.getName() + "') {");
			writer.indented(() -> writeTaskCustomization(writer, task));
			writer.println("}");
		});
	}

	@Override
	protected String invocationAsString(GradleTask.Invocation invocation) {
		String arguments = (invocation.getArguments().isEmpty()) ? "()"
				: " " + String.join(", ", invocation.getArguments());
		return invocation.getTarget() + arguments;
	}

	@Override
	protected void writeProperty(IndentingWriter writer, String name, String value) {
		if (value != null) {
			writer.println(String.format("%s = '%s'", name, value));
		}
	}

	private <T, U> void writeNestedMap(IndentingWriter writer, String name, Map<T, U> map,
			BiFunction<T, U, String> converter) {
		if (!map.isEmpty()) {
			writer.println(name + " {");
			writer.indented(() -> writeMap(writer, map, converter));
			writer.println("}");
		}
	}

}
