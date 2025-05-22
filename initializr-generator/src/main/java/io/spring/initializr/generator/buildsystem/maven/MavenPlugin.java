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

package io.spring.initializr.generator.buildsystem.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.spring.initializr.generator.version.VersionReference;

/**
 * A plugin in a {@link MavenBuild}.
 *
 * @author Andy Wilkinson
 * @author Olga Maciaszek-Sharma
 * @author Maurice Zeijen
 */
public class MavenPlugin {

	private final String groupId;

	private final String artifactId;

	private final VersionReference version;

	private final boolean extensions;

	private final boolean inherited;

	private final List<Execution> executions;

	private final List<Dependency> dependencies;

	private final Configuration configuration;

	/**
	 * Creates a new instance.
	 * @param builder the builder to use
	 */
	protected MavenPlugin(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
		this.extensions = builder.extensions;
		this.inherited = builder.inherited;
		this.executions = builder.executions.values().stream().map(ExecutionBuilder::build).toList();
		this.dependencies = List.copyOf(builder.dependencies);
		this.configuration = (builder.configurationBuilder == null) ? null : builder.configurationBuilder.build();
	}

	/**
	 * Return the group ID of the plugin.
	 * @return the group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * Return the artifact ID of the plugin.
	 * @return the artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Return the version of the plugin or {@code null} if the version of the plugin is
	 * managed. Also returns {@code null} if the version references a property.
	 * @return the version or {@code null}
	 * @deprecated for removal in favor of {@link #getVersionReference()}
	 */
	@Deprecated(forRemoval = true)
	public String getVersion() {
		return (this.version != null) ? this.version.getValue() : null;
	}

	/**
	 * Return the version of the plugin or {@code null} if the version of the plugin is
	 * managed.
	 * @return the version or {@code null}
	 */
	public VersionReference getVersionReference() {
		return this.version;
	}

	/**
	 * Return whether to load extensions of this plugin.
	 * @return {@code true} to load extensions
	 */
	public boolean isExtensions() {
		return this.extensions;
	}

	/**
	 * Return whether to inherit plugin configuration to child POMs.
	 * @return whether to inherit plugin configuration
	 */
	public boolean isInherited() {
		return this.inherited;
	}

	/**
	 * Return the {@linkplain Execution executions} of the plugin.
	 * @return the executions
	 */
	public List<Execution> getExecutions() {
		return this.executions;
	}

	/**
	 * Return the {@linkplain Dependency dependencies} of the plugin.
	 * @return the dependencies
	 */
	public List<Dependency> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Return the {@linkplain Configuration configuration} of the plugin.
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Builder for a {@link MavenPlugin}.
	 */
	public static class Builder {

		private final String groupId;

		private final String artifactId;

		private VersionReference version;

		private boolean extensions;

		private boolean inherited = true;

		private final Map<String, ExecutionBuilder> executions = new LinkedHashMap<>();

		private final List<Dependency> dependencies = new ArrayList<>();

		private ConfigurationBuilder configurationBuilder;

		/**
		 * Creates a new instance.
		 * @param groupId the group id
		 * @param artifactId the artifact id
		 */
		protected Builder(String groupId, String artifactId) {
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		/**
		 * Set the version of the plugin or {@code null} if the version is managed by the
		 * project.
		 * @param version the version of the plugin or {@code null}
		 * @return this for method chaining
		 */
		public Builder version(String version) {
			if (version == null) {
				return versionReference(null);
			}
			return versionReference(VersionReference.ofValue(version));
		}

		/**
		 * Set the version of the plugin or {@code null} if the version is managed by the
		 * project.
		 * @param version the version of the plugin or {@code null}
		 * @return this for method chaining
		 */
		public Builder versionReference(VersionReference version) {
			this.version = version;
			return this;
		}

		/**
		 * Set whether to load extensions of this plugin.
		 * @param extensions whether to load extensions
		 * @return this for method chaining
		 */
		public Builder extensions(boolean extensions) {
			this.extensions = extensions;
			return this;
		}

		/**
		 * Set whether to inherit plugin configuration to child POMs.
		 * @param inherited whether to inherit plugin configuration
		 * @return this for method chaining
		 */
		public Builder inherited(boolean inherited) {
			this.inherited = inherited;
			return this;
		}

		/**
		 * Customize the {@code configuration} of the plugin using the specified consumer.
		 * @param configuration a consumer of the current configuration
		 * @return this for method chaining
		 */
		public Builder configuration(Consumer<ConfigurationBuilder> configuration) {
			if (this.configurationBuilder == null) {
				this.configurationBuilder = new ConfigurationBuilder();
			}
			configuration.accept(this.configurationBuilder);
			return this;
		}

		/**
		 * Add an {@code execution} with the specified id and {@link Consumer} to
		 * customize the object. If the execution has already been¬ added, the consumer
		 * can be used to further tune the existing plugin execution
		 * @param id the id of the execution
		 * @param execution a {@link Consumer} to customize the {@link Execution}
		 * @return this for method chaining
		 */
		public Builder execution(String id, Consumer<ExecutionBuilder> execution) {
			execution.accept(this.executions.computeIfAbsent(id, (key) -> new ExecutionBuilder(id)));
			return this;
		}

		/**
		 * Add a plugin dependency.
		 * @param groupId the group ID of the dependency
		 * @param artifactId the artifact ID of the dependency
		 * @param version the version of the dependency
		 * @return this for method chaining
		 */
		public Builder dependency(String groupId, String artifactId, String version) {
			return dependency(groupId, artifactId, (version != null) ? VersionReference.ofValue(version) : null);
		}

		/**
		 * Add a plugin dependency.
		 * @param groupId the group ID of the dependency
		 * @param artifactId the artifact ID of the dependency
		 * @param version the version of the dependency
		 * @return this for method chaining
		 */
		public Builder dependency(String groupId, String artifactId, VersionReference version) {
			this.dependencies.add(new Dependency(groupId, artifactId, version));
			return this;
		}

		/**
		 * Build a {@link MavenPlugin} with the current state of this builder.
		 * @return a {@link MavenBuild}
		 */
		public MavenPlugin build() {
			return new MavenPlugin(this);
		}

	}

	/**
	 * Builder for an {@link Execution}.
	 */
	public static class ExecutionBuilder {

		private final String id;

		private String phase;

		private final List<String> goals = new ArrayList<>();

		private final List<String> processingInstructions = new ArrayList<>();

		private ConfigurationBuilder configurationCustomization = null;

		/**
		 * Creates a new instance.
		 * @param id the id
		 */
		public ExecutionBuilder(String id) {
			this.id = id;
		}

		Execution build() {
			return new Execution(this.id, this.phase, this.goals,
					(this.configurationCustomization == null) ? null : this.configurationCustomization.build(),
					this.processingInstructions);
		}

		/**
		 * Set the {@code phase} of the build lifecycle that goals will execute in.
		 * @param phase the phase to use
		 * @return this for method chaining
		 */
		public ExecutionBuilder phase(String phase) {
			this.phase = phase;
			return this;
		}

		/**
		 * Add a goal to invoke for this execution.
		 * @param goal the goal to invoke
		 * @return this for method chaining
		 */
		public ExecutionBuilder goal(String goal) {
			this.goals.add(goal);
			return this;
		}

		/**
		 * Add a processing instruction to invoke for this execution.
		 * @param content the processing instruction to add
		 * @return this for method chaining
		 */
		public ExecutionBuilder processingInstruction(String content) {
			this.processingInstructions.add(content);
			return this;
		}

		/**
		 * Add a m2e hint, e.g. "execute onConfiguration,onIncremental" or "ignore".
		 * @param hint the hint to add
		 * @return this for method chaining
		 */
		public ExecutionBuilder m2e(String hint) {
			return processingInstruction("m2e " + hint);
		}

		/**
		 * Customize the {@code configuration} of the execution using the specified
		 * consumer.
		 * @param configuration a consumer of the current configuration
		 * @return this for method chaining
		 */
		public ExecutionBuilder configuration(Consumer<ConfigurationBuilder> configuration) {
			if (this.configurationCustomization == null) {
				this.configurationCustomization = new ConfigurationBuilder();
			}
			configuration.accept(this.configurationCustomization);
			return this;
		}

	}

	/**
	 * Builder for a {@link Configuration}.
	 */
	public static class ConfigurationBuilder {

		private final List<Setting> settings = new ArrayList<>();

		/**
		 * Add the specified parameter with a single value.
		 * @param name the name of the parameter
		 * @param value the single value of the parameter
		 * @return this for method chaining
		 */
		public ConfigurationBuilder add(String name, String value) {
			this.settings.add(new Setting(name, value));
			return this;
		}

		/**
		 * Add the specified parameter with a nested structure.
		 * @param name the name of the parameter
		 * @param consumer a consumer to further configure the parameter
		 * @return this for method chaining
		 * @see #configure(String, Consumer) name
		 */
		public ConfigurationBuilder add(String name, Consumer<ConfigurationBuilder> consumer) {
			ConfigurationBuilder nestedConfiguration = new ConfigurationBuilder();
			consumer.accept(nestedConfiguration);
			this.settings.add(new Setting(name, nestedConfiguration));
			return this;
		}

		/**
		 * Configure the parameter with the specified {@code name}. If no parameter with
		 * that name exists, it is created. If the parameter already exists, the consumer
		 * can be used to further tune the nested structure.
		 * @param name the name of the parameter
		 * @param consumer a consumer to further configure the parameter
		 * @return this for method chaining
		 * @throws IllegalArgumentException if a parameter with the same name is
		 * registered with a single value
		 * @see #add(String, Consumer)
		 */
		public ConfigurationBuilder configure(String name, Consumer<ConfigurationBuilder> consumer) {
			Object value = this.settings.stream()
				.filter((candidate) -> candidate.getName().equals(name))
				.findFirst()
				.orElseGet(() -> {
					Setting nestedSetting = new Setting(name, new ConfigurationBuilder());
					this.settings.add(nestedSetting);
					return nestedSetting;
				})
				.getValue();
			if (!(value instanceof ConfigurationBuilder nestedConfiguration)) {
				throw new IllegalArgumentException(String.format(
						"Could not customize parameter '%s', a single value %s is already registered", name, value));
			}
			consumer.accept(nestedConfiguration);
			return this;
		}

		/**
		 * Build a {@link Configuration} with the current state of this builder.
		 * @return a {@link Configuration}
		 */
		Configuration build() {
			return new Configuration(
					this.settings.stream().map((entry) -> resolve(entry.getName(), entry.getValue())).toList());
		}

		private Setting resolve(String key, Object value) {
			if (value instanceof ConfigurationBuilder configurationBuilder) {
				List<Setting> values = configurationBuilder.settings.stream()
					.map((entry) -> resolve(entry.getName(), entry.getValue()))
					.toList();
				return new Setting(key, values);
			}
			else {
				return new Setting(key, value);
			}
		}

	}

	/**
	 * A {@code <configuration>} on an {@link Execution} or {@link MavenPlugin}.
	 */
	public static final class Configuration {

		private final List<Setting> settings;

		private Configuration(List<Setting> settings) {
			this.settings = List.copyOf(settings);
		}

		/**
		 * Return the {@linkplain Setting settings} of the configuration.
		 * @return the settings
		 */
		public List<Setting> getSettings() {
			return this.settings;
		}

	}

	/**
	 * A setting in a {@link Configuration}.
	 */
	public static final class Setting {

		private final String name;

		private final Object value;

		private Setting(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * Return the name of the configuration item.
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the value. Can be a nested {@link Configuration}.
		 * @return a simple value or a nested configuration
		 */
		public Object getValue() {
			return this.value;
		}

	}

	/**
	 * An {@code <execution>} of a {@link MavenPlugin}.
	 */
	public static final class Execution {

		private final String id;

		private final String phase;

		private final List<String> goals;

		private final Configuration configuration;

		private final List<String> processingInstructions;

		private Execution(String id, String phase, List<String> goals, Configuration configuration,
				List<String> processingInstructions) {
			this.id = id;
			this.phase = phase;
			this.goals = Collections.unmodifiableList(goals);
			this.configuration = configuration;
			this.processingInstructions = processingInstructions;
		}

		/**
		 * Return the id of the execution.
		 * @return the execution id
		 */
		public String getId() {
			return this.id;
		}

		/**
		 * Return the {@code phase} of the build lifecycle that goals will execute in.
		 * @return the execution phase
		 */
		public String getPhase() {
			return this.phase;
		}

		/**
		 * Return the plugin gaols that this execution should invoke.
		 * @return the execution goals
		 */
		public List<String> getGoals() {
			return this.goals;
		}

		/**
		 * Return the {@linkplain Configuration configuration} of the execution.
		 * @return the configuration
		 */
		public Configuration getConfiguration() {
			return this.configuration;
		}

		public List<String> getProcessingInstructions() {
			return this.processingInstructions;
		}

	}

	/**
	 * A {@code <dependency>} of a {@link MavenPlugin}.
	 */
	public static final class Dependency {

		private final String groupId;

		private final String artifactId;

		private final VersionReference version;

		private Dependency(String groupId, String artifactId, VersionReference version) {
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.version = version;
		}

		/**
		 * Return the group ID of the plugin dependency.
		 * @return the group ID
		 */
		public String getGroupId() {
			return this.groupId;
		}

		/**
		 * Return the artifact ID of the plugin dependency.
		 * @return the artifact ID
		 */
		public String getArtifactId() {
			return this.artifactId;
		}

		/**
		 * Return the version of the plugin dependency.
		 * @return the version
		 * @deprecated for removal in favor of {@link #getVersionReference()}.
		 */
		@Deprecated(forRemoval = true)
		public String getVersion() {
			return (this.version != null) ? this.version.getValue() : null;
		}

		/**
		 * Return the version of the plugin dependency.
		 * @return the version
		 */
		public VersionReference getVersionReference() {
			return this.version;
		}

	}

}
