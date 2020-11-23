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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.MavenRepositoryContainer;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSettings.Builder;

/**
 * Maven-specific {@linkplain Build build configuration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MavenBuild extends Build {

	private final MavenBuildSettings.Builder settings = new Builder();

	private final MavenResourceContainer resources = new MavenResourceContainer();

	private final MavenResourceContainer testResources = new MavenResourceContainer();

	private final MavenPluginContainer plugins = new MavenPluginContainer();

	private final MavenDistributionManagement.Builder distributionManagement = new MavenDistributionManagement.Builder();

	private final MavenProfileContainer profiles;

	public MavenBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
		this.profiles = new MavenProfileContainer(determineBuildItemResolver(buildItemResolver));
	}

	public MavenBuild() {
		this(null);
	}

	@Override
	public MavenBuildSettings.Builder settings() {
		return this.settings;
	}

	@Override
	public MavenBuildSettings getSettings() {
		return this.settings.build();
	}

	/**
	 * Return a builder to configure the {@linkplain MavenDistributionManagement
	 * distribution management} of this build.
	 * @return a builder for {@link MavenDistributionManagement}
	 */
	public MavenDistributionManagement.Builder distributionManagement() {
		return this.distributionManagement;
	}

	/**
	 * Return the {@linkplain MavenDistributionManagement distribution management} of this
	 * build.
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
	 * Return the {@linkplain MavenProfileContainer profile container} to use to configure
	 * profiles.
	 * @return the {@link MavenProfileContainer}
	 */
	public MavenProfileContainer profiles() {
		return this.profiles;
	}

}
