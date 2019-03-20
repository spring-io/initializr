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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyComparator;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.ConfigurationCustomization;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.TaskCustomization;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link GradleBuild} writer for {@code build.gradle}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class GradleBuildWriter {

	public void writeTo(IndentingWriter writer, GradleBuild build) throws IOException {
		boolean buildScriptWritten = writeBuildscript(writer, build);
		writePlugins(writer, build, buildScriptWritten);
		writeProperty(writer, "group", build.getGroup());
		writeProperty(writer, "version", build.getVersion());
		writeProperty(writer, "sourceCompatibility", build.getSourceCompatibility());
		writeConfigurations(writer, build);
		writeRepositories(writer, build, writer::println);
		writeProperties(writer, build);
		writeDependencies(writer, build);
		writeBoms(writer, build);
		writeTaskCustomizations(writer, build);
	}

	private boolean writeBuildscript(IndentingWriter writer, GradleBuild build) {
		List<String> dependencies = build.getBuildscript().getDependencies();
		Map<String, String> ext = build.getBuildscript().getExt();
		if (dependencies.isEmpty() && ext.isEmpty()) {
			return false;
		}
		writer.println("buildscript {");
		writer.indented(() -> {
			writeBuildscriptExt(writer, build);
			writeBuildscriptRepositories(writer, build);
			writeBuildscriptDependencies(writer, build);
		});
		writer.println("}");
		return true;
	}

	private void writeBuildscriptExt(IndentingWriter writer, GradleBuild build) {
		writeNestedMap(writer, "ext", build.getBuildscript().getExt(),
				(key, value) -> key + " = " + value);
	}

	private void writeBuildscriptRepositories(IndentingWriter writer, GradleBuild build) {
		writeRepositories(writer, build);
	}

	private void writeBuildscriptDependencies(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "dependencies",
				build.getBuildscript().getDependencies(),
				(dependency) -> "classpath '" + dependency + "'");
	}

	private void writePlugins(IndentingWriter writer, GradleBuild build,
			boolean buildScriptWritten) {
		writeNestedCollection(writer, "plugins", build.getPlugins(), this::pluginAsString,
				determineBeforeWriting(buildScriptWritten, writer));
		writeCollection(writer, build.getAppliedPlugins(),
				(plugin) -> "apply plugin: '" + plugin + "'", writer::println);
		writer.println();
	}

	private Runnable determineBeforeWriting(boolean buildScriptWritten,
			IndentingWriter writer) {
		if (buildScriptWritten) {
			return writer::println;
		}
		return null;
	}

	private String pluginAsString(GradlePlugin plugin) {
		String string = "id '" + plugin.getId() + "'";
		if (plugin.getVersion() != null) {
			string += " version '" + plugin.getVersion() + "'";
		}
		return string;
	}

	private void writeConfigurations(IndentingWriter writer, GradleBuild build) {
		Map<String, ConfigurationCustomization> configurationCustomizations = build
				.getConfigurationCustomizations();
		if (configurationCustomizations.isEmpty()) {
			return;
		}
		writer.println();
		writer.println("configurations {");
		writer.indented(() -> configurationCustomizations.forEach((name,
				customization) -> writeConfiguration(writer, name, customization)));
		writer.println("}");
	}

	private void writeConfiguration(IndentingWriter writer, String configurationName,
			ConfigurationCustomization configurationCustomization) {
		if (configurationCustomization.getExtendsFrom().isEmpty()) {
			writer.println(configurationName);
		}
		else {
			writer.println(configurationName + " {");
			writer.indented(() -> writer.println(String.format("extendsFrom %s",
					String.join(", ", configurationCustomization.getExtendsFrom()))));
			writer.println("}");
		}
	}

	private void writeRepositories(IndentingWriter writer, GradleBuild build) {
		writeRepositories(writer, build, null);
	}

	private void writeRepositories(IndentingWriter writer, GradleBuild build,
			Runnable beforeWriting) {
		writeNestedCollection(writer, "repositories",
				build.repositories().items().collect(Collectors.toList()),
				this::repositoryAsString, beforeWriting);
	}

	private String repositoryAsString(MavenRepository repository) {
		if (MavenRepository.MAVEN_CENTRAL.equals(repository)) {
			return "mavenCentral()";
		}
		return "maven { url '" + repository.getUrl() + "' }";
	}

	private void writeProperties(IndentingWriter writer, GradleBuild build) {
		if (build.getExt().isEmpty() && build.getVersionProperties().isEmpty()) {
			return;
		}
		Map<String, String> allProperties = new LinkedHashMap<>(build.getExt());
		build.getVersionProperties().entrySet().forEach((entry) -> allProperties
				.put(getVersionPropertyKey(entry), entry.getValue()));
		writeNestedCollection(writer, "ext", allProperties.entrySet(),
				(e) -> getFormattedProperty(e.getKey(), e.getValue()), writer::println);
	}

	private String getVersionPropertyKey(Entry<VersionProperty, String> entry) {
		return entry.getKey().isInternal() ? entry.getKey().toCamelCaseFormat()
				: entry.getKey().toStandardFormat();
	}

	private String getFormattedProperty(String key, String value) {
		return String.format("set('%s', '%s')", key, value);
	}

	private void writeDependencies(IndentingWriter writer, GradleBuild build) {
		Set<Dependency> sortedDependencies = new LinkedHashSet<>();
		DependencyContainer dependencies = build.dependencies();
		sortedDependencies
				.addAll(filterDependencies(dependencies, DependencyScope.COMPILE));
		sortedDependencies
				.addAll(filterDependencies(dependencies, DependencyScope.COMPILE_ONLY));
		sortedDependencies
				.addAll(filterDependencies(dependencies, DependencyScope.RUNTIME));
		sortedDependencies.addAll(
				filterDependencies(dependencies, DependencyScope.ANNOTATION_PROCESSOR));
		sortedDependencies.addAll(
				filterDependencies(dependencies, DependencyScope.PROVIDED_RUNTIME));
		sortedDependencies
				.addAll(filterDependencies(dependencies, DependencyScope.TEST_COMPILE));
		sortedDependencies
				.addAll(filterDependencies(dependencies, DependencyScope.TEST_RUNTIME));
		writeNestedCollection(writer, "dependencies", sortedDependencies,
				this::dependencyAsString, writer::println);
	}

	private String dependencyAsString(Dependency dependency) {
		String quoteStyle = determineQuoteStyle(dependency.getVersion());
		String version = determineVersion(dependency.getVersion());
		String type = dependency.getType();
		return configurationForScope(dependency.getScope()) + " " + quoteStyle
				+ dependency.getGroupId() + ":" + dependency.getArtifactId()
				+ ((version != null) ? ":" + version : "")
				+ ((type != null) ? "@" + type : "") + quoteStyle;
	}

	protected String configurationForScope(DependencyScope type) {
		switch (type) {
		case ANNOTATION_PROCESSOR:
			return "annotationProcessor";
		case COMPILE:
			return "implementation";
		case COMPILE_ONLY:
			return "compileOnly";
		case PROVIDED_RUNTIME:
			return "providedRuntime";
		case RUNTIME:
			return "runtimeOnly";
		case TEST_COMPILE:
			return "testImplementation";
		case TEST_RUNTIME:
			return "testRuntimeOnly";
		default:
			throw new IllegalStateException(
					"Unrecognized dependency type '" + type + "'");
		}
	}

	private void writeBoms(IndentingWriter writer, GradleBuild build) {
		if (build.boms().isEmpty()) {
			return;
		}
		List<BillOfMaterials> boms = build.boms().items()
				.sorted(Comparator.comparingInt(BillOfMaterials::getOrder).reversed())
				.collect(Collectors.toList());
		writer.println();
		writer.println("dependencyManagement {");
		writer.indented(
				() -> writeNestedCollection(writer, "imports", boms, this::bomAsString));
		writer.println("}");
	}

	private String bomAsString(BillOfMaterials bom) {
		String quoteStyle = determineQuoteStyle(bom.getVersion());
		String version = determineVersion(bom.getVersion());
		return "mavenBom " + quoteStyle + bom.getGroupId() + ":" + bom.getArtifactId()
				+ ":" + version + quoteStyle;
	}

	private String determineQuoteStyle(VersionReference versionReference) {
		return (versionReference != null && versionReference.isProperty()) ? "\"" : "'";
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference != null) {
			if (versionReference.isProperty()) {
				VersionProperty property = versionReference.getProperty();
				return "${"
						+ (property.isInternal() ? property.toCamelCaseFormat()
								: "property('" + property.toStandardFormat() + "')")
						+ "}";
			}
			return versionReference.getValue();
		}
		return null;
	}

	private void writeTaskCustomizations(IndentingWriter writer, GradleBuild build) {
		Map<String, TaskCustomization> taskCustomizations = build.getTaskCustomizations();
		taskCustomizations.forEach((name, customization) -> {
			writer.println();
			writer.println(name + " {");
			writer.indented(() -> writeTaskCustomization(writer, customization));
			writer.println("}");
		});
	}

	private void writeTaskCustomization(IndentingWriter writer,
			TaskCustomization customization) {
		writeCollection(writer, customization.getInvocations(),
				(invocation) -> invocation.getTarget() + " "
						+ String.join(", ", invocation.getArguments()));
		writeMap(writer, customization.getAssignments(),
				(key, value) -> key + " = " + value);
		customization.getNested().forEach((property, nestedCustomization) -> {
			writer.println(property + " {");
			writer.indented(() -> writeTaskCustomization(writer, nestedCustomization));
			writer.println("}");
		});
	}

	private <T> void writeNestedCollection(IndentingWriter writer, String name,
			Collection<T> collection, Function<T, String> itemToStringConverter) {
		this.writeNestedCollection(writer, name, collection, itemToStringConverter, null);
	}

	private <T> void writeNestedCollection(IndentingWriter writer, String name,
			Collection<T> collection, Function<T, String> converter,
			Runnable beforeWriting) {
		if (!collection.isEmpty()) {
			if (beforeWriting != null) {
				beforeWriting.run();
			}
			writer.println(name + " {");
			writer.indented(() -> writeCollection(writer, collection, converter));
			writer.println("}");

		}
	}

	private <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			Function<T, String> converter) {
		writeCollection(writer, collection, converter, null);
	}

	private <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			Function<T, String> itemToStringConverter, Runnable beforeWriting) {
		if (!collection.isEmpty()) {
			if (beforeWriting != null) {
				beforeWriting.run();
			}
			collection.stream().map(itemToStringConverter).forEach(writer::println);
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

	private <T, U> void writeMap(IndentingWriter writer, Map<T, U> map,
			BiFunction<T, U, String> converter) {
		map.forEach((key, value) -> writer.println(converter.apply(key, value)));
	}

	private void writeProperty(IndentingWriter writer, String name, String value) {
		if (value != null) {
			writer.println(String.format("%s = '%s'", name, value));
		}
	}

	private static Collection<Dependency> filterDependencies(
			DependencyContainer dependencies, DependencyScope... types) {
		List<DependencyScope> candidates = Arrays.asList(types);
		return dependencies.items().filter((dep) -> candidates.contains(dep.getScope()))
				.sorted(DependencyComparator.INSTANCE).collect(Collectors.toList());
	}

}
