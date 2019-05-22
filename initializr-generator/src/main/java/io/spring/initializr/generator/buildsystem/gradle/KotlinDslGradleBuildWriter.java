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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.ConfigurationCustomization;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.TaskCustomization;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link GradleBuild} writer for {@code build.gradle.kts}.
 *
 * @author Jean-Baptiste Nizet
 */
public class KotlinDslGradleBuildWriter extends GradleBuildWriter {

	private static final Map<String, String> sourceCompatibilitiesToJavaVersion = createSourceCompatibilitiesToJavaVersion();

	private static Map<String, String> createSourceCompatibilitiesToJavaVersion() {
		Map<String, String> result = new HashMap<>();
		for (int version = 6; version <= 10; version++) {
			result.put(Integer.toString(version), "VERSION_1_" + version);
			result.put("1." + version, "VERSION_1_" + version);
		}
		for (int version = 11; version <= 12; version++) {
			result.put(Integer.toString(version), "VERSION_" + version);
			result.put("1." + version, "VERSION_" + version);
		}

		return Collections.unmodifiableMap(result);
	}

	@Override
	protected void writeBuildscript(IndentingWriter writer, GradleBuild build) {
		if (!(build.getBuildscript().getDependencies().isEmpty()
				&& build.getBuildscript().getExt().isEmpty())) {
			throw new IllegalStateException(
					"build.gradle.kts scripts shouldn't need a buildscript");
		}
	}

	@Override
	protected void writePlugins(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "plugins", build.getPlugins(), this::pluginAsString,
				null);
		writer.println();

		if (!build.getAppliedPlugins().isEmpty()) {
			throw new IllegalStateException(
					"build.gradle.kts scripts shouldn't apply plugins. They should use the plugins block instead.");
		}
	}

	private String pluginAsString(GradlePlugin plugin) {
		String result = shortPluginNotation(plugin.getId());
		if (result == null) {
			result = "id(\"" + plugin.getId() + "\")";
		}
		if (plugin.getVersion() != null) {
			result += " version \"" + plugin.getVersion() + "\"";
		}
		return result;
	}

	private String shortPluginNotation(String pluginId) {
		if (pluginId.equals("java") || pluginId.equals("war")
				|| pluginId.equals("groovy")) {
			return pluginId;
		}

		String kotlinPluginPrefix = "org.jetbrains.kotlin.";
		if (pluginId.startsWith(kotlinPluginPrefix)) {
			return "kotlin(\"" + pluginId.substring(kotlinPluginPrefix.length()) + "\")";
		}

		return null;
	}

	@Override
	protected void writeJavaSourceCompatibility(IndentingWriter writer,
			GradleBuild build) {
		writer.println("java.sourceCompatibility = JavaVersion."
				+ sourceCompatibilitiesToJavaVersion.get(build.getSourceCompatibility()));
	}

	@Override
	protected void writeConfiguration(IndentingWriter writer, String configurationName,
			ConfigurationCustomization configurationCustomization) {
		if (configurationCustomization.getExtendsFrom().isEmpty()) {
			writer.println(configurationName);
		}
		else {
			writer.println(configurationName + " {");
			writer.indented(() -> writer.println(String.format("extendsFrom(%s)",
					configurationCustomization.getExtendsFrom().stream()
							.map((c) -> "configurations." + c + ".get()")
							.collect(Collectors.joining(", ")))));
			writer.println("}");
		}
	}

	@Override
	protected String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { url = uri(\"" + repository.getUrl() + "\") }";
	}

	@Override
	protected void writeDependency(IndentingWriter writer, Dependency dependency) {
		String version = determineVersion(dependency.getVersion());
		String type = dependency.getType();
		writer.print(configurationForScope(dependency.getScope()) + "(\""
				+ dependency.getGroupId() + ":" + dependency.getArtifactId()
				+ ((version != null) ? ":" + version : "")
				+ ((type != null) ? "@" + type : "") + "\")");
		if (!dependency.getExclusions().isEmpty()) {
			writer.println(" {");
			writer.indented(() -> writeCollection(writer, dependency.getExclusions(),
					this::dependencyExclusionAsString));
			writer.println("}");
		}
		else {
			writer.println();
		}
	}

	private String dependencyExclusionAsString(Exclusion exclusion) {
		return "exclude(group = \"" + exclusion.getGroupId() + "\", module = \""
				+ exclusion.getArtifactId() + "\")";
	}

	@Override
	protected void writeExtraProperties(IndentingWriter writer,
			Map<String, String> allProperties) {
		writeCollection(writer, allProperties.entrySet(),
				(e) -> getFormattedExtraProperty(e.getKey(), e.getValue()),
				writer::println);
	}

	private String getFormattedExtraProperty(String key, String value) {
		return String.format("extra[\"%s\"] = %s", key, value);
	}

	@Override
	protected String bomAsString(BillOfMaterials bom) {
		return "mavenBom(\"" + bom.getGroupId() + ":" + bom.getArtifactId() + ":"
				+ determineVersion(bom.getVersion()) + "\")";
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference != null) {
			if (versionReference.isProperty()) {
				VersionProperty property = versionReference.getProperty();
				return "${property(\"" + (property.isInternal()
						? property.toCamelCaseFormat() : property.toStandardFormat())
						+ "\")}";
			}
			return versionReference.getValue();
		}
		return null;
	}

	@Override
	protected void writeTasksWithTypeCustomizations(IndentingWriter writer,
			GradleBuild build) {
		Map<String, TaskCustomization> tasksWithTypeCustomizations = build
				.getTasksWithTypeCustomizations();

		tasksWithTypeCustomizations.forEach((typeName, customization) -> {
			writer.println();
			writer.println("tasks.withType<" + typeName + "> {");
			writer.indented(() -> writeTaskCustomization(writer, customization));
			writer.println("}");
		});
	}

	@Override
	protected void writeTaskCustomizations(IndentingWriter writer, GradleBuild build) {
		Map<String, TaskCustomization> taskCustomizations = build.getTaskCustomizations();

		taskCustomizations.forEach((name, customization) -> {
			writer.println();
			writer.println("tasks." + name + " {");
			writer.indented(() -> writeTaskCustomization(writer, customization));
			writer.println("}");
		});
	}

	@Override
	protected String invocationAsString(TaskCustomization.Invocation invocation) {
		return invocation.getTarget() + "(" + String.join(", ", invocation.getArguments())
				+ ")";
	}

	@Override
	protected void writeProperty(IndentingWriter writer, String name, String value) {
		if (value != null) {
			writer.println(String.format("%s = \"%s\"", name, value));
		}
	}

}
