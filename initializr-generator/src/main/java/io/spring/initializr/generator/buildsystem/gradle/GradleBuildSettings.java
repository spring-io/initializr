/*
 * Copyright 2012-2021 the original author or authors.
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
import java.util.List;

import io.spring.initializr.generator.buildsystem.BuildSettings;
import io.spring.initializr.generator.buildsystem.Dependency;

/**
 * Gradle-specific {@linkplain BuildSettings build settings}.
 *
 * @author Stephane Nicoll
 */
public class GradleBuildSettings extends BuildSettings {

	private final String sourceCompatibility;

	private final List<PluginMapping> pluginMappings;

	protected GradleBuildSettings(Builder builder) {
		super(builder);
		this.sourceCompatibility = builder.sourceCompatibility;
		this.pluginMappings = new ArrayList<>(builder.pluginMappings);
	}

	/**
	 * Return the java version compatibility to use when compiling Java source.
	 * @return the java version to use for source.
	 */
	public String getSourceCompatibility() {
		return this.sourceCompatibility;
	}

	/**
	 * Return the {@link PluginMapping plugin mappings}, if any.
	 * @return the plugin mappings
	 */
	public List<PluginMapping> getPluginMappings() {
		return this.pluginMappings;
	}

	/**
	 * Builder for {@link GradleBuildSettings}.
	 */
	public static class Builder extends BuildSettings.Builder<Builder> {

		private String sourceCompatibility;

		private final List<PluginMapping> pluginMappings = new ArrayList<>();

		/**
		 * Set the java version compatibility to use when compiling Java source.
		 * @param sourceCompatibility java version compatibility
		 * @return this for method chaining
		 */
		public Builder sourceCompatibility(String sourceCompatibility) {
			this.sourceCompatibility = sourceCompatibility;
			return self();
		}

		/**
		 * Map the plugin with the specified id to the specified {@link Dependency}. This
		 * is mandatory when a plugin does not have an appropriate plugin marker artifact.
		 * @param id the id of a plugin
		 * @param pluginDependency the dependency for that plugin
		 * @return this for method chaining
		 */
		public Builder mapPlugin(String id, Dependency pluginDependency) {
			if (pluginDependency.getVersion() == null || pluginDependency.getVersion().isProperty()) {
				throw new IllegalArgumentException("Mapping for plugin '" + id + "' must have a version");
			}
			this.pluginMappings.add(new PluginMapping(id, pluginDependency));
			return this;
		}

		/**
		 * Build a {@link GradleBuildSettings} with the current state of this builder.
		 * @return a {@link GradleBuildSettings}
		 */
		public GradleBuildSettings build() {
			return new GradleBuildSettings(this);
		}

	}

	/**
	 * Map a plugin identifier to a plugin implementation artifact.
	 */
	public static class PluginMapping {

		private final String id;

		private final Dependency dependency;

		PluginMapping(String id, Dependency dependency) {
			this.id = id;
			this.dependency = dependency;
		}

		/**
		 * Return the id of the plugin.
		 * @return the plugin id
		 */
		public String getId() {
			return this.id;
		}

		/**
		 * Return the plugin implementation dependency.
		 * @return the plugin implementation
		 */
		public Dependency getDependency() {
			return this.dependency;
		}

	}

}
