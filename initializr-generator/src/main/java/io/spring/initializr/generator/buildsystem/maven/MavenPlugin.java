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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A plugin in a {@link MavenBuild}.
 *
 * @author Andy Wilkinson
 */
public class MavenPlugin {

	private final String groupId;

	private final String artifactId;

	private String version;

	private final Map<String, ExecutionBuilder> executions = new LinkedHashMap<>();

	private final List<Dependency> dependencies = new ArrayList<>();

	private ConfigurationCustomization configurationCustomization = null;

	public MavenPlugin(String groupId, String artifactId) {
		this(groupId, artifactId, null);
	}

	public MavenPlugin(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void configuration(Consumer<ConfigurationCustomization> consumer) {
		if (this.configurationCustomization == null) {
			this.configurationCustomization = new ConfigurationCustomization();
		}
		consumer.accept(this.configurationCustomization);
	}

	public void execution(String id, Consumer<ExecutionBuilder> customizer) {
		customizer.accept(
				this.executions.computeIfAbsent(id, (key) -> new ExecutionBuilder(id)));
	}

	public List<Execution> getExecutions() {
		return this.executions.values().stream().map(ExecutionBuilder::build)
				.collect(Collectors.toList());
	}

	public void dependency(String groupId, String artifactId, String version) {
		this.dependencies.add(new Dependency(groupId, artifactId, version));
	}

	public List<Dependency> getDependencies() {
		return Collections.unmodifiableList(this.dependencies);
	}

	public Configuration getConfiguration() {
		return (this.configurationCustomization == null) ? null
				: this.configurationCustomization.build();
	}

	/**
	 * Builder for creation an {@link Execution}.
	 */
	public static class ExecutionBuilder {

		private final String id;

		private String phase;

		private List<String> goals = new ArrayList<>();

		private ConfigurationCustomization configurationCustomization = null;

		public ExecutionBuilder(String id) {
			this.id = id;
		}

		Execution build() {
			return new Execution(this.id, this.phase, this.goals,
					(this.configurationCustomization == null) ? null
							: this.configurationCustomization.build());
		}

		public ExecutionBuilder phase(String phase) {
			this.phase = phase;
			return this;
		}

		public ExecutionBuilder goal(String goal) {
			this.goals.add(goal);
			return this;
		}

		public void configuration(Consumer<ConfigurationCustomization> consumer) {
			if (this.configurationCustomization == null) {
				this.configurationCustomization = new ConfigurationCustomization();
			}
			consumer.accept(this.configurationCustomization);
		}

	}

	/**
	 * Customization of a {@link Configuration}.
	 */
	public static class ConfigurationCustomization {

		private final List<Setting> settings = new ArrayList<>();

		/**
		 * Add the specified parameter with a single value.
		 * @param name the name of the parameter
		 * @param value the single value of the parameter
		 * @return this for method chaining
		 */
		public ConfigurationCustomization add(String name, String value) {
			this.settings.add(new Setting(name, value));
			return this;
		}

		/**
		 * Configure the parameter with the specified {@code name}.
		 * @param name the name of the parameter
		 * @param consumer a consumer to further configure the parameter
		 * @return this for method chaining
		 * @throws IllegalArgumentException if a parameter with the same name is
		 * registered with a single value
		 */
		public ConfigurationCustomization configure(String name,
				Consumer<ConfigurationCustomization> consumer) {
			Object value = this.settings.stream()
					.filter((candidate) -> candidate.getName().equals(name)).findFirst()
					.orElseGet(() -> {
						Setting nestedSetting = new Setting(name,
								new ConfigurationCustomization());
						this.settings.add(nestedSetting);
						return nestedSetting;
					}).getValue();
			if (!(value instanceof ConfigurationCustomization)) {
				throw new IllegalArgumentException(String.format(
						"Could not customize parameter '%s', a single value %s is already registered",
						name, value));
			}
			ConfigurationCustomization nestedConfiguration = (ConfigurationCustomization) value;
			consumer.accept(nestedConfiguration);
			return this;
		}

		Configuration build() {
			return new Configuration(this.settings.stream()
					.map((entry) -> resolve(entry.getName(), entry.getValue()))
					.collect(Collectors.toList()));
		}

		private Setting resolve(String key, Object value) {
			if (value instanceof ConfigurationCustomization) {
				List<Setting> values = ((ConfigurationCustomization) value).settings
						.stream()
						.map((entry) -> resolve(entry.getName(), entry.getValue()))
						.collect(Collectors.toList());
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
			this.settings = settings;
		}

		public List<Setting> getSettings() {
			return Collections.unmodifiableList(this.settings);
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

		public String getName() {
			return this.name;
		}

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

		private Execution(String id, String phase, List<String> goals,
				Configuration configuration) {
			this.id = id;
			this.phase = phase;
			this.goals = goals;
			this.configuration = configuration;
		}

		public String getId() {
			return this.id;
		}

		public String getPhase() {
			return this.phase;
		}

		public List<String> getGoals() {
			return this.goals;
		}

		public Configuration getConfiguration() {
			return this.configuration;
		}

	}

	/**
	 * A {@code <dependency>} of a {@link MavenPlugin}.
	 */
	public static final class Dependency {

		private final String groupId;

		private final String artifactId;

		private final String version;

		private Dependency(String groupId, String artifactId, String version) {
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.version = version;
		}

		public String getGroupId() {
			return this.groupId;
		}

		public String getArtifactId() {
			return this.artifactId;
		}

		public String getVersion() {
			return this.version;
		}

	}

}
