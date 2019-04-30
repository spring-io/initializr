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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Gradle build configuration for a project.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
public class GradleBuild extends Build {

	private String sourceCompatibility;

	private final Map<String, String> ext = new TreeMap<>();

	private final List<GradlePlugin> plugins = new ArrayList<>();

	private final List<String> appliedPlugins = new ArrayList<>();

	private final Map<String, ConfigurationCustomization> configurationCustomizations = new LinkedHashMap<>();

	private final Map<String, TaskCustomization> taskCustomizations = new LinkedHashMap<>();

	private final Set<String> importedTypes = new HashSet<>();

	private final Map<String, TaskCustomization> tasksWithTypeCustomizations = new LinkedHashMap<>();

	private final Buildscript buildscript = new Buildscript();

	public GradleBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
	}

	public GradleBuild() {
		this(null);
	}

	public void setSourceCompatibility(String sourceCompatibility) {
		this.sourceCompatibility = sourceCompatibility;
	}

	public String getSourceCompatibility() {
		return this.sourceCompatibility;
	}

	public GradleBuild ext(String key, String value) {
		this.ext.put(key, value);
		return this;
	}

	public Map<String, String> getExt() {
		return Collections.unmodifiableMap(this.ext);
	}

	public GradlePlugin addPlugin(String id) {
		return this.addPlugin(id, null);
	}

	public GradlePlugin addPlugin(String id, String version) {
		GradlePlugin plugin = new GradlePlugin(id, version);
		this.plugins.add(plugin);
		return plugin;
	}

	public void applyPlugin(String id) {
		this.appliedPlugins.add(id);
	}

	public List<GradlePlugin> getPlugins() {
		return Collections.unmodifiableList(this.plugins);
	}

	public List<String> getAppliedPlugins() {
		return Collections.unmodifiableList(this.appliedPlugins);
	}

	public void buildscript(Consumer<Buildscript> customizer) {
		customizer.accept(this.buildscript);
	}

	public Buildscript getBuildscript() {
		return this.buildscript;
	}

	public void customizeConfiguration(String configurationName,
			Consumer<ConfigurationCustomization> customizer) {
		customizer.accept(this.configurationCustomizations.computeIfAbsent(
				configurationName, (name) -> new ConfigurationCustomization()));
	}

	public void addConfiguration(String configurationName) {
		customizeConfiguration(configurationName, (configuration) -> {
		});
	}

	public Map<String, ConfigurationCustomization> getConfigurationCustomizations() {
		return Collections.unmodifiableMap(this.configurationCustomizations);
	}

	public Set<String> getImportedTypes() {
		return Collections.unmodifiableSet(this.importedTypes);
	}

	/**
	 * Customize tasks matching a given type.
	 * @param typeName the name of type. Can use the short form for well-known types such
	 * as {@code JavaCompile}, use a fully qualified name if an import is required
	 * @param customizer a callback to customize tasks matching that type
	 */
	public void customizeTasksWithType(String typeName,
			Consumer<TaskCustomization> customizer) {
		String packageName = ClassUtils.getPackageName(typeName);
		if (!StringUtils.isEmpty(packageName)) {
			this.importedTypes.add(typeName);
		}
		String shortName = ClassUtils.getShortName(typeName);
		customizer.accept(this.tasksWithTypeCustomizations.computeIfAbsent(shortName,
				(name) -> new TaskCustomization()));
	}

	public Map<String, TaskCustomization> getTasksWithTypeCustomizations() {
		return Collections.unmodifiableMap(this.tasksWithTypeCustomizations);
	}

	public void customizeTask(String taskName, Consumer<TaskCustomization> customizer) {
		customizer.accept(this.taskCustomizations.computeIfAbsent(taskName,
				(name) -> new TaskCustomization()));
	}

	public Map<String, TaskCustomization> getTaskCustomizations() {
		return Collections.unmodifiableMap(this.taskCustomizations);
	}

	/**
	 * The {@code buildscript} block in the {@code build.gradle} file.
	 */
	public static class Buildscript {

		private final List<String> dependencies = new ArrayList<>();

		private final Map<String, String> ext = new LinkedHashMap<>();

		public Buildscript dependency(String coordinates) {
			this.dependencies.add(coordinates);
			return this;
		}

		public Buildscript ext(String key, String value) {
			this.ext.put(key, value);
			return this;
		}

		public List<String> getDependencies() {
			return Collections.unmodifiableList(this.dependencies);
		}

		public Map<String, String> getExt() {
			return Collections.unmodifiableMap(this.ext);
		}

	}

	/**
	 * Customization of a configuration in a Gradle build.
	 */
	public static class ConfigurationCustomization {

		private final Set<String> extendsFrom = new LinkedHashSet<>();

		public void extendsFrom(String configurationName) {
			this.extendsFrom.add(configurationName);
		}

		public Set<String> getExtendsFrom() {
			return Collections.unmodifiableSet(this.extendsFrom);
		}

	}

	/**
	 * Customization of a task in a Gradle build.
	 */
	public static class TaskCustomization {

		private final List<Invocation> invocations = new ArrayList<>();

		private final Map<String, String> assignments = new LinkedHashMap<>();

		private final Map<String, TaskCustomization> nested = new LinkedHashMap<>();

		public void nested(String property, Consumer<TaskCustomization> customizer) {
			customizer.accept(this.nested.computeIfAbsent(property,
					(name) -> new TaskCustomization()));
		}

		public Map<String, TaskCustomization> getNested() {
			return this.nested;
		}

		public void invoke(String target, String... arguments) {
			this.invocations.add(new Invocation(target, Arrays.asList(arguments)));
		}

		public List<Invocation> getInvocations() {
			return Collections.unmodifiableList(this.invocations);
		}

		public void set(String target, String value) {
			this.assignments.put(target, value);
		}

		public Map<String, String> getAssignments() {
			return Collections.unmodifiableMap(this.assignments);
		}

		/**
		 * An invocation of a method that customizes a task.
		 */
		public static class Invocation {

			private final String target;

			private final List<String> arguments;

			Invocation(String target, List<String> arguments) {
				this.target = target;
				this.arguments = arguments;
			}

			public String getTarget() {
				return this.target;
			}

			public List<String> getArguments() {
				return this.arguments;
			}

		}

	}

}
