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

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.BomContainer;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.MavenRepositoryContainer;
import io.spring.initializr.generator.buildsystem.PropertyContainer;

/**
 * A profile in a {@link MavenBuild}.
 *
 * @author Daniel Andres Pelaez Lopez
 * @author Stephane Nicoll
 */
public class MavenProfile {

	private final String id;

	private final MavenProfileActivation.Builder activation = new MavenProfileActivation.Builder();

	private final SettingsBuilder settings = new SettingsBuilder();

	private final PropertyContainer properties = new PropertyContainer();

	private final DependencyContainer dependencies;

	private final MavenResourceContainer resources = new MavenResourceContainer();

	private final MavenResourceContainer testResources = new MavenResourceContainer();

	private final MavenPluginContainer plugins = new MavenPluginContainer();

	private final BomContainer boms;

	private final MavenRepositoryContainer repositories;

	private final MavenRepositoryContainer pluginRepositories;

	private final MavenDistributionManagement.Builder distributionManagement = new MavenDistributionManagement.Builder();

	protected MavenProfile(String id, BuildItemResolver buildItemResolver) {
		this.id = id;
		this.dependencies = new DependencyContainer(buildItemResolver::resolveDependency);
		this.boms = new BomContainer(buildItemResolver::resolveBom);
		this.repositories = new MavenRepositoryContainer(buildItemResolver::resolveRepository);
		this.pluginRepositories = new MavenRepositoryContainer(buildItemResolver::resolveRepository);
	}

	/**
	 * Return the identifier of the profile.
	 * @return the profile id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return a builder to configure how this profile should be
	 * {@link MavenProfileActivation activated}.
	 * @return a builder for {@link MavenProfileActivation}.
	 */
	public MavenProfileActivation.Builder activation() {
		return this.activation;
	}

	/**
	 * Return the {@link MavenProfileActivation} of this profile.
	 * @return the {@link MavenProfileActivation}
	 */
	public MavenProfileActivation getActivation() {
		return this.activation.build();
	}

	/**
	 * Return a builder to configure the general settings of this profile.
	 * @return a builder for {@link SettingsBuilder}.
	 */
	public SettingsBuilder settings() {
		return this.settings;
	}

	/**
	 * Return the settings of this profile.
	 * @return a {@link Settings}
	 */
	public Settings getSettings() {
		return this.settings.build();
	}

	/**
	 * Return the {@linkplain PropertyContainer property container} to use to configure
	 * properties.
	 * @return the {@link PropertyContainer}
	 */
	public PropertyContainer properties() {
		return this.properties;
	}

	/**
	 * Return the {@linkplain DependencyContainer dependency container} to use to
	 * configure dependencies.
	 * @return the {@link DependencyContainer}
	 */
	public DependencyContainer dependencies() {
		return this.dependencies;
	}

	/**
	 * Return the {@linkplain BomContainer bom container} to use to configure Bill of
	 * Materials.
	 * @return the {@link BomContainer}
	 */
	public BomContainer boms() {
		return this.boms;
	}

	/**
	 * Return the {@linkplain MavenRepositoryContainer repository container} to use to
	 * configure repositories.
	 * @return the {@link MavenRepositoryContainer} for repositories
	 */
	public MavenRepositoryContainer repositories() {
		return this.repositories;
	}

	/**
	 * Return the {@linkplain MavenRepositoryContainer repository container} to use to
	 * configure plugin repositories.
	 * @return the {@link MavenRepositoryContainer} for plugin repositories
	 */
	public MavenRepositoryContainer pluginRepositories() {
		return this.pluginRepositories;
	}

	/**
	 * Return a builder to configure the {@linkplain MavenDistributionManagement
	 * distribution management} of this profile.
	 * @return a builder for {@link MavenDistributionManagement}
	 */
	public MavenDistributionManagement.Builder distributionManagement() {
		return this.distributionManagement;
	}

	/**
	 * Return the {@linkplain MavenDistributionManagement distribution management} of this
	 * profile.
	 * @return the {@link MavenDistributionManagement}
	 */
	public MavenDistributionManagement getDistributionManagement() {
		return this.distributionManagement.build();
	}

	/**
	 * Return the {@linkplain MavenResource resource container} to use to configure main
	 * resources.
	 * @return the {@link MavenRepositoryContainer} for main resources
	 */
	public MavenResourceContainer resources() {
		return this.resources;
	}

	/**
	 * Return the {@linkplain MavenResource resource container} to use to configure test
	 * resources.
	 * @return the {@link MavenRepositoryContainer} for test resources
	 */
	public MavenResourceContainer testResources() {
		return this.testResources;
	}

	/**
	 * Return the {@linkplain MavenPluginContainer plugin container} to use to configure
	 * plugins.
	 * @return the {@link MavenPluginContainer}
	 */
	public MavenPluginContainer plugins() {
		return this.plugins;
	}

	/**
	 * Builder for {@link Settings}.
	 */
	public static class SettingsBuilder {

		private String defaultGoal;

		private String finalName;

		protected SettingsBuilder() {
		}

		/**
		 * Set the default goal or phase to execute if none is given when this profile is
		 * active.
		 * @param defaultGoal the default goal or {@code null} to use the value in the
		 * build
		 * @return this for method chaining
		 */
		public SettingsBuilder defaultGoal(String defaultGoal) {
			this.defaultGoal = defaultGoal;
			return this;
		}

		/**
		 * Set the name of the bundled project when it is finally built when this profile
		 * is active.
		 * @param finalName the final name of the artifact or {@code null} to use the
		 * value in the build.
		 * @return this for method chaining
		 */
		public SettingsBuilder finalName(String finalName) {
			this.finalName = finalName;
			return this;
		}

		public Settings build() {
			return new Settings(this);
		}

	}

	/**
	 * Maven profile settings.
	 */
	public static final class Settings {

		private final String defaultGoal;

		private final String finalName;

		protected Settings(SettingsBuilder builder) {
			this.defaultGoal = builder.defaultGoal;
			this.finalName = builder.finalName;
		}

		/**
		 * Return the default goal or phase to execute if none is given.
		 * @return the default goal or {@code null} to use the default
		 */
		public String getDefaultGoal() {
			return this.defaultGoal;
		}

		/**
		 * Return the final name of the artifact.
		 * @return the final name or {@code null} to use the default
		 */
		public String getFinalName() {
			return this.finalName;
		}

	}

}
