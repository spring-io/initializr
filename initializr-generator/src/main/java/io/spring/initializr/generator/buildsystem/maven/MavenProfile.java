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

import io.spring.initializr.generator.buildsystem.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MavenProfile {

	private final String id;

	private final MavenProfileActivation activation;

	private final MavenProfileBuild build;

	private final List<String> modules;

	private final MavenRepositoryContainer repositories;

	private final MavenRepositoryContainer pluginRepositories;

	private final DependencyContainer dependencies;

	private final MavenReporting reporting;

	private final BomContainer dependencyManagement;

	private final MavenDistributionManagement distributionManagement;

	private final MavenConfiguration properties;

	protected MavenProfile(Builder builder) {
		this.id = builder.id;
		this.activation = Optional.ofNullable(builder.activationBuilder).map(MavenProfileActivation.Builder::build)
				.orElse(null);
		this.build = Optional.ofNullable(builder.buildBuilder).map(MavenProfileBuild.Builder::build).orElse(null);
		this.modules = builder.modules;
		this.repositories = builder.repositories;
		this.pluginRepositories = builder.pluginRepositories;
		this.dependencies = builder.dependencies;
		this.reporting = Optional.ofNullable(builder.reportingBuilder).map(MavenReporting.Builder::build).orElse(null);
		this.dependencyManagement = builder.dependencyManagement;
		this.distributionManagement = Optional.ofNullable(builder.distributionManagementBuilder)
				.map(MavenDistributionManagement.Builder::build).orElse(null);
		this.properties = Optional.ofNullable(builder.properties).map(MavenConfiguration.Builder::build).orElse(null);
	}

	public String getId() {
		return this.id;
	}

	public MavenProfileActivation getActivation() {
		return this.activation;
	}

	public MavenProfileBuild getBuild() {
		return this.build;
	}

	public List<String> getModules() {
		return this.modules;
	}

	public MavenRepositoryContainer getRepositories() {
		return this.repositories;
	}

	public MavenRepositoryContainer getPluginRepositories() {
		return this.pluginRepositories;
	}

	public DependencyContainer getDependencies() {
		return this.dependencies;
	}

	public MavenReporting getReporting() {
		return this.reporting;
	}

	public BomContainer getDependencyManagement() {
		return this.dependencyManagement;
	}

	public MavenDistributionManagement getDistributionManagement() {
		return this.distributionManagement;
	}

	public MavenConfiguration getProperties() {
		return this.properties;
	}

	public static class Builder {

		private final String id;

		private final BuildItemResolver buildItemResolver;

		private MavenProfileActivation.Builder activationBuilder;

		private MavenProfileBuild.Builder buildBuilder;

		private List<String> modules;

		private MavenRepositoryContainer repositories;

		private MavenRepositoryContainer pluginRepositories;

		private DependencyContainer dependencies;

		private MavenReporting.Builder reportingBuilder;

		private BomContainer dependencyManagement;

		private MavenDistributionManagement.Builder distributionManagementBuilder;

		private MavenConfiguration.Builder properties;

		protected Builder(String id, BuildItemResolver buildItemResolver) {
			this.id = id;
			this.buildItemResolver = buildItemResolver;
		}

		public Builder activation(Consumer<MavenProfileActivation.Builder> activation) {
			if (this.activationBuilder == null) {
				this.activationBuilder = new MavenProfileActivation.Builder();
			}
			activation.accept(this.activationBuilder);
			return this;
		}

		public Builder build(Consumer<MavenProfileBuild.Builder> build) {
			if (this.buildBuilder == null) {
				this.buildBuilder = new MavenProfileBuild.Builder();
			}
			build.accept(this.buildBuilder);
			return this;
		}

		public Builder module(String module) {
			if (this.modules == null) {
				this.modules = new LinkedList<>();
			}
			this.modules.add(module);
			return this;
		}

		public Builder repositories(Consumer<MavenRepositoryContainer> repositories) {
			if (this.repositories == null) {
				this.repositories = new MavenRepositoryContainer(this.buildItemResolver::resolveRepository);
			}
			repositories.accept(this.repositories);
			return this;
		}

		public Builder pluginRepositories(Consumer<MavenRepositoryContainer> pluginRepositories) {
			if (this.pluginRepositories == null) {
				this.pluginRepositories = new MavenRepositoryContainer(this.buildItemResolver::resolveRepository);
			}
			pluginRepositories.accept(this.pluginRepositories);
			return this;
		}

		public Builder reporting(Consumer<MavenReporting.Builder> reporting) {
			if (this.reportingBuilder == null) {
				this.reportingBuilder = new MavenReporting.Builder();
			}
			reporting.accept(this.reportingBuilder);
			return this;
		}

		public Builder dependencies(Consumer<DependencyContainer> dependencies) {
			if (this.dependencies == null) {
				this.dependencies = new DependencyContainer(this.buildItemResolver::resolveDependency);
			}
			dependencies.accept(this.dependencies);
			return this;
		}

		public Builder dependencyManagement(Consumer<BomContainer> dependencyManagement) {
			if (this.dependencyManagement == null) {
				this.dependencyManagement = new BomContainer(this.buildItemResolver::resolveBom);
			}
			dependencyManagement.accept(this.dependencyManagement);
			return this;
		}

		public Builder distributionManagement(
				Consumer<MavenDistributionManagement.Builder> distributionManagementBuilder) {
			if (this.distributionManagementBuilder == null) {
				this.distributionManagementBuilder = new MavenDistributionManagement.Builder();
			}
			distributionManagementBuilder.accept(this.distributionManagementBuilder);
			return this;
		}

		public Builder properties(Consumer<MavenConfiguration.Builder> properties) {
			if (this.properties == null) {
				this.properties = new MavenConfiguration.Builder();
			}
			properties.accept(this.properties);
			return this;
		}

		public MavenProfile build() {
			return new MavenProfile(this);
		}

	}

}
