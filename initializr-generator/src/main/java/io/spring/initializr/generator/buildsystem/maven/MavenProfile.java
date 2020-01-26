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

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.MavenRepositoryContainer;
import io.spring.initializr.generator.buildsystem.PropertyContainer;

import java.util.LinkedList;
import java.util.List;
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

    private final DependencyContainer dependencyManagement;

    private final MavenDistributionManagement distributionManagement;

    private final PropertyContainer properties;

    protected MavenProfile(Builder builder) {
        this.id = builder.id;
        this.activation = (builder.activationBuilder == null) ? null : builder.activationBuilder.build();
        this.build = (builder.buildBuilder == null) ? null : builder.buildBuilder.build();
        this.modules = builder.modules;
        this.repositories = builder.repositories;
		this.pluginRepositories = builder.pluginRepositories;
		this.dependencies = builder.dependencies;
		this.reporting =  (builder.reportingBuilder == null) ? null : builder.reportingBuilder.build();
		this.dependencyManagement = builder.dependencyManagement;
        this.distributionManagement = (builder.distributionManagementBuilder == null) ? null : builder.distributionManagementBuilder.build();
        this.properties = builder.properties;
    }

    public String getId() {
        return id;
    }

    public static class Builder {

        private final String id;

        private MavenProfileActivation.Builder activationBuilder = new MavenProfileActivation.Builder();

        private MavenProfileBuild.Builder buildBuilder = new MavenProfileBuild.Builder();

		private List<String> modules = new LinkedList<>();

		private MavenRepositoryContainer repositories;

		private MavenRepositoryContainer pluginRepositories;

        private DependencyContainer dependencies;

        private MavenReporting.Builder reportingBuilder = new MavenReporting.Builder();

        private DependencyContainer dependencyManagement;

        private MavenDistributionManagement.Builder distributionManagementBuilder = new MavenDistributionManagement.Builder();

        private PropertyContainer properties = new PropertyContainer();

        protected Builder(String id, BuildItemResolver buildItemResolver) {
            this.id = id;
			this.repositories = new MavenRepositoryContainer(buildItemResolver::resolveRepository);
			this.pluginRepositories = new MavenRepositoryContainer(buildItemResolver::resolveRepository);
			this.dependencies = new DependencyContainer(buildItemResolver::resolveDependency);
            this.dependencyManagement = new DependencyContainer(buildItemResolver::resolveDependency);
        }

        public MavenProfile.Builder activation(Consumer<MavenProfileActivation.Builder> activation) {
            activation.accept(this.activationBuilder);
            return this;
        }

        public MavenProfile.Builder build(Consumer<MavenProfileBuild.Builder> build) {
            build.accept(this.buildBuilder);
            return this;
        }

		public MavenProfile.Builder module(String module) {
			this.modules.add(module);
			return this;
		}

		public MavenProfile.Builder repositories(Consumer<MavenRepositoryContainer> repositories) {
			repositories.accept(this.repositories);
			return this;
		}

		public MavenProfile.Builder pluginRepositories(Consumer<MavenRepositoryContainer> pluginRepositories) {
			pluginRepositories.accept(this.pluginRepositories);
			return this;
		}

        public MavenProfile.Builder reporting(Consumer<MavenReporting.Builder> reporting) {
            reporting.accept(this.reportingBuilder);
            return this;
        }

        public MavenProfile.Builder dependencies(Consumer<DependencyContainer> dependencies) {
            dependencies.accept(this.dependencies);
            return this;
        }

        public MavenProfile.Builder dependencyManagement(Consumer<DependencyContainer> dependencyManagement) {
            dependencyManagement.accept(this.dependencyManagement);
            return this;
        }

        public MavenProfile.Builder distributionManagement(Consumer<MavenDistributionManagement.Builder> distributionManagementBuilder) {
            distributionManagementBuilder.accept(this.distributionManagementBuilder);
            return this;
        }

        public MavenProfile.Builder properties(Consumer<PropertyContainer> properties) {
            properties.accept(this.properties);
            return this;
        }

        public MavenProfile build() {
            return new MavenProfile(this);
        }
    }
}
