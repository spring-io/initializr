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

import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSettings.Builder;

/**
 * Gradle-specific {@linkplain Build build configuration}.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
public class GradleBuild extends Build {

	private final GradleBuildSettings.Builder settings = new Builder();

	private final GradlePluginContainer plugins = new GradlePluginContainer();

	private final GradleConfigurationContainer configurations = new GradleConfigurationContainer();

	private final GradleTaskContainer tasks = new GradleTaskContainer();

	private final GradleBuildscript.Builder buildscript = new GradleBuildscript.Builder();

	/**
	 * Create a new Gradle build using the specified {@link BuildItemResolver}.
	 * @param buildItemResolver the build item resolved to use
	 */
	public GradleBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
	}

	/**
	 * Create a new Gradle build without a build item resolver.
	 */
	public GradleBuild() {
		this(null);
	}

	@Override
	public GradleBuildSettings.Builder settings() {
		return this.settings;
	}

	@Override
	public GradleBuildSettings getSettings() {
		return this.settings.build();
	}

	/**
	 * Return the {@linkplain GradlePluginContainer plugin container} to use to configure
	 * plugins.
	 * @return the {@link GradlePluginContainer}
	 */
	public GradlePluginContainer plugins() {
		return this.plugins;
	}

	/**
	 * Return the {@linkplain GradleConfigurationContainer configuration container} to use
	 * for configuration customizations.
	 * @return the {@link GradleConfigurationContainer}
	 */
	public GradleConfigurationContainer configurations() {
		return this.configurations;
	}

	/**
	 * Return the {@linkplain GradleTaskContainer task container} to use to configure
	 * tasks.
	 * @return the {@link GradleTaskContainer}
	 */
	public GradleTaskContainer tasks() {
		return this.tasks;
	}

	/**
	 * Customize the {@code buildscript} of the build using the specified consumer.
	 * @param buildscript a consumer of the current buildscript
	 */
	public void buildscript(Consumer<GradleBuildscript.Builder> buildscript) {
		buildscript.accept(this.buildscript);
	}

	/**
	 * Return the {@link GradleBuildscript buildscript} of this build.
	 * @return the buildscript to use
	 */
	public GradleBuildscript getBuildscript() {
		return this.buildscript.build();
	}

}
