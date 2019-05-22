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

/**
 * {@link GradleBuild} writer abstraction.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 * @see GroovyDslGradleBuildWriter
 * @see KotlinDslGradleBuildWriter
 */
public abstract class GradleBuildWriter {

	public final void writeTo(IndentingWriter writer, GradleBuild build)
			throws IOException {
		writeImports(writer, build);
		writeBuildscript(writer, build);
		writePlugins(writer, build);
		writeProperty(writer, "group", build.getGroup());
		writeProperty(writer, "version", build.getVersion());
		writeJavaSourceCompatibility(writer, build);
		writer.println();
		writeConfigurations(writer, build);
		writeRepositories(writer, build);
		writeProperties(writer, build);
		writeDependencies(writer, build);
		writeBoms(writer, build);
		writeTasksWithTypeCustomizations(writer, build);
		writeTaskCustomizations(writer, build);
	}

	private void writeImports(IndentingWriter writer, GradleBuild build) {
		build.getImportedTypes().stream().sorted().forEachOrdered(
				(importedType) -> writer.println("import " + importedType));
		if (!build.getImportedTypes().isEmpty()) {
			writer.println();
		}
	}

	protected abstract void writeBuildscript(IndentingWriter writer, GradleBuild build);

	protected abstract void writePlugins(IndentingWriter writer, GradleBuild build);

	protected abstract void writeJavaSourceCompatibility(IndentingWriter writer,
			GradleBuild build);

	private void writeConfigurations(IndentingWriter writer, GradleBuild build) {
		Map<String, ConfigurationCustomization> configurationCustomizations = build
				.getConfigurationCustomizations();
		if (configurationCustomizations.isEmpty()) {
			return;
		}
		writer.println("configurations {");
		writer.indented(() -> configurationCustomizations.forEach((name,
				customization) -> writeConfiguration(writer, name, customization)));
		writer.println("}");
		writer.println("");
	}

	protected abstract void writeConfiguration(IndentingWriter writer,
			String configurationName,
			ConfigurationCustomization configurationCustomization);

	protected final void writeRepositories(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "repositories",
				build.repositories().items().collect(Collectors.toList()),
				this::repositoryAsString);
	}

	protected abstract String repositoryAsString(MavenRepository repository);

	private void writeProperties(IndentingWriter writer, GradleBuild build) {
		if (build.getExt().isEmpty() && build.getVersionProperties().isEmpty()) {
			return;
		}
		Map<String, String> allProperties = new LinkedHashMap<>(build.getExt());
		build.getVersionProperties().entrySet().forEach((entry) -> allProperties
				.put(getVersionPropertyKey(entry), "\"" + entry.getValue() + "\""));
		writeExtraProperties(writer, allProperties);
	}

	protected abstract void writeExtraProperties(IndentingWriter writer,
			Map<String, String> allProperties);

	private String getVersionPropertyKey(Entry<VersionProperty, String> entry) {
		return entry.getKey().isInternal() ? entry.getKey().toCamelCaseFormat()
				: entry.getKey().toStandardFormat();
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
		if (!sortedDependencies.isEmpty()) {
			writer.println();
			writer.println("dependencies" + " {");
			writer.indented(() -> sortedDependencies
					.forEach((dependency) -> writeDependency(writer, dependency)));
			writer.println("}");
		}
	}

	protected abstract void writeDependency(IndentingWriter writer,
			Dependency dependency);

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

	protected abstract String bomAsString(BillOfMaterials bom);

	protected abstract void writeTasksWithTypeCustomizations(IndentingWriter writer,
			GradleBuild build);

	protected abstract void writeTaskCustomizations(IndentingWriter writer,
			GradleBuild build);

	protected final void writeTaskCustomization(IndentingWriter writer,
			TaskCustomization customization) {
		writeCollection(writer, customization.getInvocations(), this::invocationAsString);
		writeMap(writer, customization.getAssignments(),
				(key, value) -> key + " = " + value);
		customization.getNested().forEach((property, nestedCustomization) -> {
			writer.println(property + " {");
			writer.indented(() -> writeTaskCustomization(writer, nestedCustomization));
			writer.println("}");
		});
	}

	protected abstract String invocationAsString(TaskCustomization.Invocation invocation);

	protected final <T> void writeNestedCollection(IndentingWriter writer, String name,
			Collection<T> collection, Function<T, String> itemToStringConverter) {
		this.writeNestedCollection(writer, name, collection, itemToStringConverter, null);
	}

	protected final <T> void writeNestedCollection(IndentingWriter writer, String name,
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

	protected final <T> void writeCollection(IndentingWriter writer,
			Collection<T> collection, Function<T, String> converter) {
		writeCollection(writer, collection, converter, null);
	}

	protected final <T> void writeCollection(IndentingWriter writer,
			Collection<T> collection, Function<T, String> itemToStringConverter,
			Runnable beforeWriting) {
		if (!collection.isEmpty()) {
			if (beforeWriting != null) {
				beforeWriting.run();
			}
			collection.stream().map(itemToStringConverter).forEach(writer::println);
		}
	}

	protected final <T, U> void writeMap(IndentingWriter writer, Map<T, U> map,
			BiFunction<T, U, String> converter) {
		map.forEach((key, value) -> writer.println(converter.apply(key, value)));
	}

	protected abstract void writeProperty(IndentingWriter writer, String name,
			String value);

	private static Collection<Dependency> filterDependencies(
			DependencyContainer dependencies, DependencyScope... types) {
		List<DependencyScope> candidates = Arrays.asList(types);
		return dependencies.items().filter((dep) -> candidates.contains(dep.getScope()))
				.sorted(DependencyComparator.INSTANCE).collect(Collectors.toList());
	}

}
