/*
 * Copyright 2012-2020 the original author or authors.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link GradleBuild} writer for {@code build.gradle.kts}.
 *
 * @author Jean-Baptiste Nizet
 */
public class KotlinDslGradleBuildWriter extends GradleBuildWriter {

	private final Map<String, String> sourceCompatibilitiesToJavaVersion = new HashMap<>();

	@Override
	protected void writeBuildscript(IndentingWriter writer, GradleBuild build) {
		if (!(build.getBuildscript().getDependencies().isEmpty() && build.getBuildscript().getExt().isEmpty())) {
			throw new IllegalStateException("build.gradle.kts scripts shouldn't need a buildscript");
		}
	}

	@Override
	protected void writePlugins(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "plugins", extractStandardPlugin(build), this::pluginAsString, null);
		writer.println();
		if (build.plugins().values().anyMatch(GradlePlugin::isApply)) {
			throw new IllegalStateException(
					"build.gradle.kts scripts shouldn't apply plugins. They should use the plugins block instead.");
		}
	}

	private String pluginAsString(StandardGradlePlugin plugin) {
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
		if (pluginId.equals("java") || pluginId.equals("war") || pluginId.equals("groovy")) {
			return pluginId;
		}

		String kotlinPluginPrefix = "org.jetbrains.kotlin.";
		if (pluginId.startsWith(kotlinPluginPrefix)) {
			return "kotlin(\"" + pluginId.substring(kotlinPluginPrefix.length()) + "\")";
		}

		return null;
	}

	@Override
	protected void writeJavaSourceCompatibility(IndentingWriter writer, GradleBuildSettings settings) {
		writer.println("java.sourceCompatibility = " + getJavaVersionConstant(settings.getSourceCompatibility()));
	}

	private String getJavaVersionConstant(String jvmVersion) {
		return this.sourceCompatibilitiesToJavaVersion.computeIfAbsent(jvmVersion, (key) -> {
			StringBuilder sb = new StringBuilder("JavaVersion.");
			if (jvmVersion == null) {
				return sb.append("VERSION_11").toString();
			}
			int generation = (jvmVersion.startsWith("1.") ? Integer.parseInt(jvmVersion.substring(2))
					: Integer.parseInt(jvmVersion));
			if (generation >= 1 && generation <= 10) {
				sb.append("VERSION_1_").append(generation);
			}
			else if (generation <= 17) {
				sb.append("VERSION_").append(generation);
			}
			else {
				sb.append("VERSION_HIGHER");
			}
			return sb.toString();
		});
	}

	private String configurationReference(String configurationName, Collection<String> customConfigurations) {
		if (customConfigurations.contains(configurationName)) {
			return configurationName;
		}
		return "configurations." + configurationName + ".get()";
	}

	@Override
	protected String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { url = uri(\"" + repository.getUrl() + "\") }";
	}

	@Override
	protected void writeConfigurations(IndentingWriter writer, GradleConfigurationContainer configurations) {
		configurations.names()
				.forEach((configuration) -> writer.println("val " + configuration + " by configurations.creating"));
		if (!configurations.customizations().findFirst().isPresent()) {
			return;
		}
		writer.println("configurations {");
		List<String> customConfigurations = configurations.names().collect(Collectors.toList());
		writer.indented(() -> configurations.customizations()
				.forEach((configuration) -> writeConfiguration(writer, configuration, customConfigurations)));
		writer.println("}");
		writer.println("");
	}

	protected void writeConfiguration(IndentingWriter writer, GradleConfiguration configuration,
			List<String> customConfigurations) {
		if (configuration.getExtendsFrom().isEmpty()) {
			writer.println(configuration.getName());
		}
		else {
			writer.println(configuration.getName() + " {");
			writer.indented(() -> writer.println(String.format("extendsFrom(%s)",
					configuration.getExtendsFrom().stream()
							.map((name) -> configurationReference(name, customConfigurations))
							.collect(Collectors.joining(", ")))));
			writer.println("}");
		}
	}

	@Override
	protected void writeDependency(IndentingWriter writer, Dependency dependency) {
		String version = determineVersion(dependency.getVersion());
		String classifier = dependency.getClassifier();
		String type = dependency.getType();
		writer.print(configurationForDependency(dependency) + "(\"" + dependency.getGroupId() + ":"
				+ dependency.getArtifactId() + ((version != null) ? ":" + version : "")
				+ ((classifier != null) ? ":" + classifier : "") + ((type != null) ? "@" + type : "") + "\")");
		if (!dependency.getExclusions().isEmpty()) {
			writer.println(" {");
			writer.indented(
					() -> writeCollection(writer, dependency.getExclusions(), this::dependencyExclusionAsString));
			writer.println("}");
		}
		else {
			writer.println();
		}
	}

	private String dependencyExclusionAsString(Exclusion exclusion) {
		return "exclude(group = \"" + exclusion.getGroupId() + "\", module = \"" + exclusion.getArtifactId() + "\")";
	}

	@Override
	protected void writeExtraProperties(IndentingWriter writer, Map<String, String> allProperties) {
		writeCollection(writer, allProperties.entrySet(), (e) -> getFormattedExtraProperty(e.getKey(), e.getValue()),
				writer::println);
	}

	private String getFormattedExtraProperty(String key, String value) {
		return String.format("extra[\"%s\"] = %s", key, value);
	}

	@Override
	protected String bomAsString(BillOfMaterials bom) {
		return "mavenBom(\"" + bom.getGroupId() + ":" + bom.getArtifactId() + ":" + determineVersion(bom.getVersion())
				+ "\")";
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference != null) {
			if (versionReference.isProperty()) {
				VersionProperty property = versionReference.getProperty();
				return "${property(\""
						+ (property.isInternal() ? property.toCamelCaseFormat() : property.toStandardFormat()) + "\")}";
			}
			return versionReference.getValue();
		}
		return null;
	}

	@Override
	protected void writeTasks(IndentingWriter writer, GradleTaskContainer tasks) {
		tasks.values().filter((candidate) -> candidate.getType() != null).forEach((task) -> {
			writer.println();
			writer.println("tasks.withType<" + task.getName() + "> {");
			writer.indented(() -> writeTaskCustomization(writer, task));
			writer.println("}");
		});
		tasks.values().filter((candidate) -> candidate.getType() == null).forEach((task) -> {
			writer.println();
			writer.println("tasks." + task.getName() + " {");
			writer.indented(() -> writeTaskCustomization(writer, task));
			writer.println("}");
		});
	}

	@Override
	protected String invocationAsString(GradleTask.Invocation invocation) {
		return invocation.getTarget() + "(" + String.join(", ", invocation.getArguments()) + ")";
	}

	@Override
	protected void writeProperty(IndentingWriter writer, String name, String value) {
		if (value != null) {
			writer.println(String.format("%s = \"%s\"", name, value));
		}
	}

}
