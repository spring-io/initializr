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

package io.spring.initializr.generator.buildsystem.maven;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.DependencyComparator;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.PropertyContainer;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Execution;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Setting;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link MavenBuild} writer.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Olga Maciaszek-Sharma
 */
public class MavenBuildWriter {

	public void writeTo(IndentingWriter writer, MavenBuild build) throws IOException {
		MavenBuildSettings settings = build.getSettings();
		writeProject(writer, () -> {
			writeParent(writer, build);
			writeProjectCoordinates(writer, settings);
			writePackaging(writer, settings);
			writeProjectName(writer, settings);
			writeProperties(writer, build.properties());
			writeDependencies(writer, build);
			writeDependencyManagement(writer, build);
			writeBuild(writer, build);
			writeRepositories(writer, build);
		});
	}

	private void writeProject(IndentingWriter writer, Runnable whenWritten) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println(
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		writer.indented(() -> {
			writer.println(
					"xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">");
			writeSingleElement(writer, "modelVersion", "4.0.0");
			whenWritten.run();
		});
		writer.println();
		writer.println("</project>");
	}

	private void writeParent(IndentingWriter writer, MavenBuild build) {
		MavenParent parent = build.getSettings().getParent();
		if (parent == null) {
			return;
		}
		writer.println("<parent>");
		writer.indented(() -> {
			writeSingleElement(writer, "groupId", parent.getGroupId());
			writeSingleElement(writer, "artifactId", parent.getArtifactId());
			writeSingleElement(writer, "version", parent.getVersion());
			writer.println("<relativePath/> <!-- lookup parent from repository -->");
		});
		writer.println("</parent>");
	}

	private void writeProjectCoordinates(IndentingWriter writer, MavenBuildSettings settings) {
		writeSingleElement(writer, "groupId", settings.getGroup());
		writeSingleElement(writer, "artifactId", settings.getArtifact());
		writeSingleElement(writer, "version", settings.getVersion());
	}

	private void writePackaging(IndentingWriter writer, MavenBuildSettings settings) {
		String packaging = settings.getPackaging();
		if (!"jar".equals(packaging)) {
			writeSingleElement(writer, "packaging", packaging);
		}
	}

	private void writeProjectName(IndentingWriter writer, MavenBuildSettings settings) {
		writeSingleElement(writer, "name", settings.getName());
		writeSingleElement(writer, "description", settings.getDescription());
	}

	private void writeProperties(IndentingWriter writer, PropertyContainer properties) {
		if (properties.isEmpty()) {
			return;
		}
		writer.println();
		writeElement(writer, "properties", () -> {
			properties.values().forEach((entry) -> writeSingleElement(writer, entry.getKey(), entry.getValue()));
			properties.versions((VersionProperty::toStandardFormat))
					.forEach((entry) -> writeSingleElement(writer, entry.getKey(), entry.getValue()));
		});
	}

	private void writeDependencies(IndentingWriter writer, MavenBuild build) {
		if (build.dependencies().isEmpty()) {
			return;
		}
		DependencyContainer dependencies = build.dependencies();
		writer.println();
		writeElement(writer, "dependencies", () -> {
			Collection<Dependency> compiledDependencies = writeDependencies(writer, dependencies,
					(scope) -> scope == null || scope == DependencyScope.COMPILE);
			if (!compiledDependencies.isEmpty()) {
				writer.println();
			}
			writeDependencies(writer, dependencies, hasScope(DependencyScope.RUNTIME));
			writeDependencies(writer, dependencies, hasScope(DependencyScope.COMPILE_ONLY));
			writeDependencies(writer, dependencies, hasScope(DependencyScope.ANNOTATION_PROCESSOR));
			writeDependencies(writer, dependencies, hasScope(DependencyScope.PROVIDED_RUNTIME));
			writeDependencies(writer, dependencies,
					hasScope(DependencyScope.TEST_COMPILE, DependencyScope.TEST_RUNTIME));
		});
	}

	private Predicate<DependencyScope> hasScope(DependencyScope... validScopes) {
		return (scope) -> Arrays.asList(validScopes).contains(scope);
	}

	private Collection<Dependency> writeDependencies(IndentingWriter writer, DependencyContainer dependencies,
			Predicate<DependencyScope> filter) {
		Collection<Dependency> candidates = dependencies.items().filter((dep) -> filter.test(dep.getScope()))
				.sorted(DependencyComparator.INSTANCE).collect(Collectors.toList());
		writeCollection(writer, candidates, this::writeDependency);
		return candidates;
	}

	private void writeDependency(IndentingWriter writer, Dependency dependency) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", dependency.getGroupId());
			writeSingleElement(writer, "artifactId", dependency.getArtifactId());
			writeSingleElement(writer, "version", determineVersion(dependency.getVersion()));
			writeSingleElement(writer, "scope", scopeForType(dependency.getScope()));
			if (isOptional(dependency)) {
				writeSingleElement(writer, "optional", Boolean.toString(true));
			}
			writeSingleElement(writer, "type", dependency.getType());
			if (!dependency.getExclusions().isEmpty()) {
				writeElement(writer, "exclusions",
						() -> writeCollection(writer, dependency.getExclusions(), this::writeDependencyExclusion));
			}
		});
	}

	private void writeDependencyExclusion(IndentingWriter writer, Exclusion exclusion) {
		writeElement(writer, "exclusion", () -> {
			writeSingleElement(writer, "groupId", exclusion.getGroupId());
			writeSingleElement(writer, "artifactId", exclusion.getArtifactId());
		});
	}

	private String scopeForType(DependencyScope type) {
		if (type == null) {
			return null;
		}
		switch (type) {
		case ANNOTATION_PROCESSOR:
			return null;
		case COMPILE:
			return null;
		case COMPILE_ONLY:
			return null;
		case PROVIDED_RUNTIME:
			return "provided";
		case RUNTIME:
			return "runtime";
		case TEST_COMPILE:
			return "test";
		case TEST_RUNTIME:
			return "test";
		default:
			throw new IllegalStateException("Unrecognized dependency type '" + type + "'");
		}
	}

	private boolean isOptional(Dependency dependency) {
		if (dependency instanceof MavenDependency && ((MavenDependency) dependency).isOptional()) {
			return true;
		}
		return (dependency.getScope() == DependencyScope.ANNOTATION_PROCESSOR
				|| dependency.getScope() == DependencyScope.COMPILE_ONLY);
	}

	private void writeDependencyManagement(IndentingWriter writer, MavenBuild build) {
		if (build.boms().isEmpty()) {
			return;
		}
		List<BillOfMaterials> boms = build.boms().items().sorted(Comparator.comparing(BillOfMaterials::getOrder))
				.collect(Collectors.toList());
		writer.println();
		writeElement(writer, "dependencyManagement",
				() -> writeElement(writer, "dependencies", () -> writeCollection(writer, boms, this::writeBom)));
	}

	private void writeBom(IndentingWriter writer, BillOfMaterials bom) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", bom.getGroupId());
			writeSingleElement(writer, "artifactId", bom.getArtifactId());
			writeSingleElement(writer, "version", determineVersion(bom.getVersion()));
			writeSingleElement(writer, "type", "pom");
			writeSingleElement(writer, "scope", "import");
		});
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference == null) {
			return null;
		}
		return (versionReference.isProperty()) ? "${" + versionReference.getProperty().toStandardFormat() + "}"
				: versionReference.getValue();
	}

	private void writeBuild(IndentingWriter writer, MavenBuild build) {
		MavenBuildSettings settings = build.getSettings();
		if (settings.getSourceDirectory() == null && settings.getTestSourceDirectory() == null
				&& build.resources().isEmpty() && build.testResources().isEmpty() && build.plugins().isEmpty()) {
			return;
		}
		writer.println();
		writeElement(writer, "build", () -> {
			writeSingleElement(writer, "sourceDirectory", settings.getSourceDirectory());
			writeSingleElement(writer, "testSourceDirectory", settings.getTestSourceDirectory());
			writeResources(writer, build);
			writePlugins(writer, build);

		});
	}

	private void writeResources(IndentingWriter writer, MavenBuild build) {
		if (!build.resources().isEmpty()) {
			writeElement(writer, "resources", () -> writeCollection(writer,
					build.resources().values().collect(Collectors.toList()), this::writeResource));
		}
		if (!build.testResources().isEmpty()) {
			writeElement(writer, "testResources", () -> writeCollection(writer,
					build.testResources().values().collect(Collectors.toList()), this::writeTestResource));
		}
	}

	private void writeResource(IndentingWriter writer, MavenResource resource) {
		writeResource(writer, resource, "resource");
	}

	private void writeTestResource(IndentingWriter writer, MavenResource resource) {
		writeResource(writer, resource, "testResource");
	}

	private void writeResource(IndentingWriter writer, MavenResource resource, String resourceName) {
		writeElement(writer, resourceName, () -> {
			writeSingleElement(writer, "directory", resource.getDirectory());
			writeSingleElement(writer, "targetPath", resource.getTargetPath());
			if (resource.isFiltering()) {
				writeSingleElement(writer, "filtering", "true");
			}
			if (!resource.getIncludes().isEmpty()) {
				writeElement(writer, "includes",
						() -> writeCollection(writer, resource.getIncludes(), this::writeResourceInclude));
			}
			if (!resource.getExcludes().isEmpty()) {
				writeElement(writer, "excludes",
						() -> writeCollection(writer, resource.getExcludes(), this::writeResourceExclude));
			}
		});
	}

	private void writeResourceInclude(IndentingWriter writer, String include) {
		writeSingleElement(writer, "include", include);
	}

	private void writeResourceExclude(IndentingWriter writer, String exclude) {
		writeSingleElement(writer, "exclude", exclude);
	}

	private void writePlugins(IndentingWriter writer, MavenBuild build) {
		if (build.plugins().isEmpty()) {
			return;
		}
		writeElement(writer, "plugins", () -> writeCollection(writer,
				build.plugins().values().collect(Collectors.toList()), this::writePlugin));
	}

	private void writePlugin(IndentingWriter writer, MavenPlugin plugin) {
		writeElement(writer, "plugin", () -> {
			writeSingleElement(writer, "groupId", plugin.getGroupId());
			writeSingleElement(writer, "artifactId", plugin.getArtifactId());
			writeSingleElement(writer, "version", plugin.getVersion());
			if (plugin.isExtensions()) {
				writeSingleElement(writer, "extensions", "true");
			}
			writePluginConfiguration(writer, plugin.getConfiguration());
			if (!plugin.getExecutions().isEmpty()) {
				writeElement(writer, "executions",
						() -> writeCollection(writer, plugin.getExecutions(), this::writePluginExecution));
			}
			if (!plugin.getDependencies().isEmpty()) {
				writeElement(writer, "dependencies",
						() -> writeCollection(writer, plugin.getDependencies(), this::writePluginDependency));
			}
		});
	}

	private void writePluginConfiguration(IndentingWriter writer, Configuration configuration) {
		if (configuration == null || configuration.getSettings().isEmpty()) {
			return;
		}
		writeElement(writer, "configuration",
				() -> writeCollection(writer, configuration.getSettings(), this::writeSetting));
	}

	@SuppressWarnings("unchecked")
	private void writeSetting(IndentingWriter writer, Setting setting) {
		if (setting.getValue() instanceof String) {
			writeSingleElement(writer, setting.getName(), (String) setting.getValue());
		}
		else if (setting.getValue() instanceof List) {
			writeElement(writer, setting.getName(),
					() -> writeCollection(writer, (List<Setting>) setting.getValue(), this::writeSetting));
		}
	}

	private void writePluginExecution(IndentingWriter writer, Execution execution) {
		writeElement(writer, "execution", () -> {
			writeSingleElement(writer, "id", execution.getId());
			writeSingleElement(writer, "phase", execution.getPhase());
			List<String> goals = execution.getGoals();
			if (!goals.isEmpty()) {
				writeElement(writer, "goals", () -> goals.forEach((goal) -> writeSingleElement(writer, "goal", goal)));
			}
			writePluginConfiguration(writer, execution.getConfiguration());
		});
	}

	private void writePluginDependency(IndentingWriter writer, MavenPlugin.Dependency dependency) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", dependency.getGroupId());
			writeSingleElement(writer, "artifactId", dependency.getArtifactId());
			writeSingleElement(writer, "version", dependency.getVersion());
		});
	}

	private void writeRepositories(IndentingWriter writer, MavenBuild build) {
		List<MavenRepository> repositories = filterRepositories(build.repositories().items());
		List<MavenRepository> pluginRepositories = filterRepositories(build.pluginRepositories().items());
		if (repositories.isEmpty() && pluginRepositories.isEmpty()) {
			return;
		}
		writer.println();
		if (!repositories.isEmpty()) {
			writeRepositories(writer, "repositories", "repository", repositories);
		}
		if (!pluginRepositories.isEmpty()) {
			writeRepositories(writer, "pluginRepositories", "pluginRepository", pluginRepositories);
		}
	}

	private List<MavenRepository> filterRepositories(Stream<MavenRepository> repositories) {
		return repositories.filter((repository) -> !MavenRepository.MAVEN_CENTRAL.equals(repository))
				.collect(Collectors.toList());
	}

	private void writeRepositories(IndentingWriter writer, String containerName, String childName,
			List<MavenRepository> repositories) {
		writeElement(writer, containerName,
				() -> repositories.forEach((repository) -> writeElement(writer, childName, () -> {
					writeSingleElement(writer, "id", repository.getId());
					writeSingleElement(writer, "name", repository.getName());
					writeSingleElement(writer, "url", repository.getUrl());
					if (repository.isSnapshotsEnabled()) {
						writeElement(writer, "snapshots",
								() -> writeSingleElement(writer, "enabled", Boolean.toString(true)));
					}
				})));
	}

	private void writeSingleElement(IndentingWriter writer, String name, String text) {
		if (text != null) {
			writer.print(String.format("<%s>", name));
			writer.print(text);
			writer.println(String.format("</%s>", name));
		}
	}

	private void writeElement(IndentingWriter writer, String name, Runnable withContent) {
		writer.println(String.format("<%s>", name));
		writer.indented(withContent);
		writer.println(String.format("</%s>", name));
	}

	private <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			BiConsumer<IndentingWriter, T> itemWriter) {
		if (!collection.isEmpty()) {
			collection.forEach((item) -> itemWriter.accept(writer, item));
		}
	}

}
