/*
 * Copyright 2012-2024 the original author or authors.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyComparator;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.PropertyContainer;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.version.VersionProperty;

/**
 * {@link GradleBuild} writer abstraction.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 * @see GroovyDslGradleBuildWriter
 * @see KotlinDslGradleBuildWriter
 */
public abstract class GradleBuildWriter {

	/**
	 * Write a {@linkplain GradleBuild build.gradle} using the specified
	 * {@linkplain IndentingWriter writer}.
	 * @param writer the writer to use
	 * @param build the gradle build to write
	 */
	public final void writeTo(IndentingWriter writer, GradleBuild build) {
		GradleBuildSettings settings = build.getSettings();
		writeImports(writer, build.tasks(), build.snippets(), build.extensions());
		writeBuildscript(writer, build);
		writePlugins(writer, build);
		writeProperty(writer, "group", settings.getGroup());
		writeProperty(writer, "version", settings.getVersion());
		writer.println();
		writeJavaSourceCompatibility(writer, settings);
		writeToolchain(writer, settings);
		writeConfigurations(writer, build.configurations());
		writeRepositories(writer, build);
		writeProperties(writer, build.properties());
		writeDependencies(writer, build);
		writeBoms(writer, build);
		writeExtensions(writer, build.extensions());
		writeTasks(writer, build.tasks());
		writeSnippets(writer, build.snippets());
	}

	private void writeImports(IndentingWriter writer, GradleTaskContainer tasks, GradleSnippetContainer snippets,
			GradleExtensionContainer extensions) {
		List<String> imports = concat(tasks.importedTypes(), snippets.importedTypes(), extensions.importedTypes())
			.sorted()
			.toList();
		imports.forEach((importedType) -> writer.println("import " + importedType));
		if (!imports.isEmpty()) {
			writer.println();
		}
	}

	protected abstract void writeBuildscript(IndentingWriter writer, GradleBuild build);

	protected abstract void writePlugins(IndentingWriter writer, GradleBuild build);

	protected List<StandardGradlePlugin> extractStandardPlugin(GradleBuild build) {
		return build.plugins()
			.values()
			.filter(StandardGradlePlugin.class::isInstance)
			.map(StandardGradlePlugin.class::cast)
			.collect(Collectors.toList());
	}

	/**
	 * Writes the source compatibility for Java.
	 * @param writer the writer
	 * @param settings the settings
	 * @deprecated for removal in favor of Gradle toolchains
	 */
	@Deprecated(forRemoval = true)
	protected void writeJavaSourceCompatibility(IndentingWriter writer, GradleBuildSettings settings) {
	}

	protected abstract void writeConfigurations(IndentingWriter writer, GradleConfigurationContainer configurations);

	private void writeToolchain(IndentingWriter writer, GradleBuildSettings settings) {
		writer.println("java {");
		writer.indented(() -> {
			writer.println("toolchain {");
			writer.indented(() -> writer.println(
					"languageVersion = JavaLanguageVersion.of(%s)".formatted(sourceCompatibilityAsNumber(settings))));
			writer.println("}");
		});
		writer.println("}");
		writer.println("");
	}

	private static String sourceCompatibilityAsNumber(GradleBuildSettings settings) {
		String version = (settings.getSourceCompatibility() != null) ? settings.getSourceCompatibility()
				: Language.DEFAULT_JVM_VERSION;
		if (version.startsWith("1.")) {
			return version.substring("1.".length());
		}
		return version;
	}

	protected final void writeRepositories(IndentingWriter writer, GradleBuild build) {
		writeNestedCollection(writer, "repositories", build.repositories().items().collect(Collectors.toList()),
				this::repositoryAsString);
	}

	protected abstract String repositoryAsString(MavenRepository repository);

	private void writeProperties(IndentingWriter writer, PropertyContainer properties) {
		if (properties.isEmpty()) {
			return;
		}
		Map<String, String> allProperties = new LinkedHashMap<>(properties.values()
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue,
					TreeMap::new)));
		properties.versions(this::getVersionPropertyKey)
			.forEach((entry) -> allProperties.put(entry.getKey(), "\"" + entry.getValue() + "\""));
		writeExtraProperties(writer, allProperties);
	}

	protected abstract void writeExtraProperties(IndentingWriter writer, Map<String, String> allProperties);

	private String getVersionPropertyKey(VersionProperty versionProperty) {
		return versionProperty.isInternal() ? versionProperty.toCamelCaseFormat() : versionProperty.toStandardFormat();
	}

	private void writeDependencies(IndentingWriter writer, GradleBuild build) {
		Set<Dependency> sortedDependencies = new LinkedHashSet<>();
		DependencyContainer dependencies = build.dependencies();
		sortedDependencies
			.addAll(filterDependencies(dependencies, (scope) -> scope == null || scope == DependencyScope.COMPILE));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.COMPILE_ONLY)));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.RUNTIME)));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.ANNOTATION_PROCESSOR)));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.PROVIDED_RUNTIME)));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.TEST_COMPILE)));
		sortedDependencies.addAll(filterDependencies(dependencies, hasScope(DependencyScope.TEST_RUNTIME)));
		if (!sortedDependencies.isEmpty()) {
			writer.println();
			writer.println("dependencies" + " {");
			writer.indented(() -> sortedDependencies.forEach((dependency) -> writeDependency(writer, dependency)));
			writer.println("}");
		}
	}

	private Predicate<DependencyScope> hasScope(DependencyScope... validScopes) {
		return (scope) -> Arrays.asList(validScopes).contains(scope);
	}

	/**
	 * Return the {@link Comparator} to use to sort dependencies.
	 * @return a dependency comparator
	 */
	protected Comparator<Dependency> getDependencyComparator() {
		return DependencyComparator.INSTANCE;
	}

	protected abstract void writeDependency(IndentingWriter writer, Dependency dependency);

	protected String configurationForDependency(Dependency dependency) {
		if (dependency instanceof GradleDependency) {
			String configuration = ((GradleDependency) dependency).getConfiguration();
			if (configuration != null) {
				return configuration;
			}
		}
		DependencyScope type = dependency.getScope();
		if (type == null) {
			return "implementation";
		}
		return switch (type) {
			case ANNOTATION_PROCESSOR -> "annotationProcessor";
			case COMPILE -> "implementation";
			case COMPILE_ONLY -> "compileOnly";
			case PROVIDED_RUNTIME -> "providedRuntime";
			case RUNTIME -> "runtimeOnly";
			case TEST_COMPILE -> "testImplementation";
			case TEST_RUNTIME -> "testRuntimeOnly";
		};
	}

	private void writeBoms(IndentingWriter writer, GradleBuild build) {
		if (build.boms().isEmpty()) {
			return;
		}
		List<BillOfMaterials> boms = build.boms()
			.items()
			.sorted(Comparator.comparingInt(BillOfMaterials::getOrder).reversed())
			.collect(Collectors.toList());
		writer.println();
		writer.println("dependencyManagement {");
		writer.indented(() -> writeNestedCollection(writer, "imports", boms, this::bomAsString));
		writer.println("}");
	}

	protected abstract String bomAsString(BillOfMaterials bom);

	protected abstract void writeTasks(IndentingWriter writer, GradleTaskContainer tasks);

	private void writeExtensions(IndentingWriter writer, GradleExtensionContainer extensions) {
		extensions.values().forEach((extension) -> {
			writer.println();
			writer.println(extension.getName() + " {");
			writer.indented(() -> writeExtensionCustomization(writer, extension));
			writer.println("}");
		});

	}

	private void writeExtensionCustomization(IndentingWriter writer, GradleExtension extension) {
		writeCollection(writer, extension.getInvocations(), this::invocationAsString);
		writeCollection(writer, extension.getAttributes(), this::attributeAsString);
		extension.getNested().forEach((ignored, nested) -> {
			writer.println(nested.getName() + " {");
			writer.indented(() -> writeExtensionCustomization(writer, nested));
			writer.println("}");
		});
	}

	protected final void writeTaskCustomization(IndentingWriter writer, GradleTask task) {
		writeCollection(writer, task.getInvocations(), this::invocationAsString);
		writeCollection(writer, task.getAttributes(), this::attributeAsString);
		task.getNested().forEach((property, nestedCustomization) -> {
			writer.println(property + " {");
			writer.indented(() -> writeTaskCustomization(writer, nestedCustomization));
			writer.println("}");
		});
	}

	private String attributeAsString(Attribute attribute) {
		String separator = (attribute.getType() == Attribute.Type.SET) ? "=" : "+=";
		return String.format("%s %s %s", attribute.getName(), separator, attribute.getValue());
	}

	protected abstract String invocationAsString(Invocation invocation);

	private void writeSnippets(IndentingWriter writer, GradleSnippetContainer snippets) {
		if (!snippets.isEmpty()) {
			writer.println();
		}
		snippets.values().forEach((snippet) -> {
			snippet.apply(writer);
			writer.println();
		});
	}

	protected final <T> void writeNestedCollection(IndentingWriter writer, String name, Collection<T> collection,
			Function<T, String> itemToStringConverter) {
		this.writeNestedCollection(writer, name, collection, itemToStringConverter, null);
	}

	protected final <T> void writeNestedCollection(IndentingWriter writer, String name, Collection<T> collection,
			Function<T, String> converter, Runnable beforeWriting) {
		if (!collection.isEmpty()) {
			if (beforeWriting != null) {
				beforeWriting.run();
			}
			writer.println(name + " {");
			writer.indented(() -> writeCollection(writer, collection, converter));
			writer.println("}");

		}
	}

	protected final <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			Function<T, String> converter) {
		writeCollection(writer, collection, converter, null);
	}

	protected final <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			Function<T, String> itemToStringConverter, Runnable beforeWriting) {
		if (!collection.isEmpty()) {
			if (beforeWriting != null) {
				beforeWriting.run();
			}
			collection.stream().map(itemToStringConverter).forEach(writer::println);
		}
	}

	protected final <T, U> void writeMap(IndentingWriter writer, Map<T, U> map, BiFunction<T, U, String> converter) {
		map.forEach((key, value) -> writer.println(converter.apply(key, value)));
	}

	protected abstract void writeProperty(IndentingWriter writer, String name, String value);

	private Collection<Dependency> filterDependencies(DependencyContainer dependencies,
			Predicate<DependencyScope> filter) {
		return dependencies.items()
			.filter((dep) -> filter.test(dep.getScope()))
			.sorted(getDependencyComparator())
			.collect(Collectors.toList());
	}

	@SafeVarargs
	private static Stream<String> concat(Stream<String>... streams) {
		Stream<String> result = Stream.empty();
		for (Stream<String> stream : streams) {
			result = Stream.concat(result, stream);
		}
		return result;
	}

}
