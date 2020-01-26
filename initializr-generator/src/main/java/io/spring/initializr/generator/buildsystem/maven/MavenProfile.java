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

import static java.util.Optional.ofNullable;

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
        this.activation = ofNullable(builder.activationBuilder)
                .map(MavenProfileActivation.Builder::build)
                .orElse(null);
        this.build = ofNullable(builder.buildBuilder)
                .map(MavenProfileBuild.Builder::build)
                .orElse(null);
        this.modules = builder.modules;
        this.repositories = builder.repositories;
        this.pluginRepositories = builder.pluginRepositories;
        this.dependencies = builder.dependencies;
        this.reporting = ofNullable(builder.reportingBuilder)
                .map(MavenReporting.Builder::build)
                .orElse(null);
        this.dependencyManagement = builder.dependencyManagement;
        this.distributionManagement = ofNullable(builder.distributionManagementBuilder)
                .map(MavenDistributionManagement.Builder::build)
                .orElse(null);
        this.properties = builder.properties;
    }

    public String getId() {
        return id;
    }

    public MavenProfileActivation getActivation() {
        return activation;
    }

    public MavenProfileBuild getBuild() {
        return build;
    }

    public List<String> getModules() {
        return modules;
    }

    public MavenRepositoryContainer getRepositories() {
        return repositories;
    }

    public MavenRepositoryContainer getPluginRepositories() {
        return pluginRepositories;
    }

    public DependencyContainer getDependencies() {
        return dependencies;
    }

    public MavenReporting getReporting() {
        return reporting;
    }

    public DependencyContainer getDependencyManagement() {
        return dependencyManagement;
    }

    public MavenDistributionManagement getDistributionManagement() {
        return distributionManagement;
    }

    public PropertyContainer getProperties() {
        return properties;
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

        private DependencyContainer dependencyManagement;

        private MavenDistributionManagement.Builder distributionManagementBuilder;

        private PropertyContainer properties;

        protected Builder(String id, BuildItemResolver buildItemResolver) {
            this.id = id;
            this.buildItemResolver = buildItemResolver;
        }

        public MavenProfile.Builder activation(Consumer<MavenProfileActivation.Builder> activation) {
            if (this.activationBuilder == null) {
                this.activationBuilder = new MavenProfileActivation.Builder();
            }
            activation.accept(this.activationBuilder);
            return this;
        }

        public MavenProfile.Builder build(Consumer<MavenProfileBuild.Builder> build) {
            if (this.buildBuilder == null) {
                this.buildBuilder = new MavenProfileBuild.Builder();
            }
            build.accept(this.buildBuilder);
            return this;
        }

        public MavenProfile.Builder module(String module) {
            if (this.modules == null) {
                this.modules = new LinkedList<>();
            }
            this.modules.add(module);
            return this;
        }

        public MavenProfile.Builder repositories(Consumer<MavenRepositoryContainer> repositories) {
            if (this.repositories == null) {
                this.repositories = new MavenRepositoryContainer(this.buildItemResolver::resolveRepository);
            }
            repositories.accept(this.repositories);
            return this;
        }

        public MavenProfile.Builder pluginRepositories(Consumer<MavenRepositoryContainer> pluginRepositories) {
            if (this.pluginRepositories == null) {
                this.pluginRepositories = new MavenRepositoryContainer(this.buildItemResolver::resolveRepository);
            }
            pluginRepositories.accept(this.pluginRepositories);
            return this;
        }

        public MavenProfile.Builder reporting(Consumer<MavenReporting.Builder> reporting) {
            if (this.reportingBuilder == null) {
                this.reportingBuilder = new MavenReporting.Builder();
            }
            reporting.accept(this.reportingBuilder);
            return this;
        }

        public MavenProfile.Builder dependencies(Consumer<DependencyContainer> dependencies) {
            if (this.dependencies == null) {
                this.dependencies = new DependencyContainer(this.buildItemResolver::resolveDependency);
            }
            dependencies.accept(this.dependencies);
            return this;
        }

        public MavenProfile.Builder dependencyManagement(Consumer<DependencyContainer> dependencyManagement) {
            if (this.dependencyManagement == null) {
                this.dependencyManagement = new DependencyContainer(this.buildItemResolver::resolveDependency);
            }
            dependencyManagement.accept(this.dependencyManagement);
            return this;
        }

        public MavenProfile.Builder distributionManagement(Consumer<MavenDistributionManagement.Builder> distributionManagementBuilder) {
            if (this.distributionManagementBuilder == null) {
                this.distributionManagementBuilder = new MavenDistributionManagement.Builder();
            }
            distributionManagementBuilder.accept(this.distributionManagementBuilder);
            return this;
        }

        public MavenProfile.Builder properties(Consumer<PropertyContainer> properties) {
            if (this.properties == null) {
                this.properties = new PropertyContainer();
            }
            properties.accept(this.properties);
            return this;
        }

        public MavenProfile build() {
            return new MavenProfile(this);
        }
    }
}
