/*
 * Copyright 2012-2024 the original author or authors.
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

/**
 * Encapsulates the various build components for a Gradle build.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 * @author Akshat Gulati
 */
public class GradleBuildComponents {

	private final GradleBuildSettings.Builder settings = new GradleBuildSettings.Builder();

	private final GradlePluginContainer plugins = new GradlePluginContainer();

	private final GradleConfigurationContainer configurations = new GradleConfigurationContainer();

	private final GradleTaskContainer tasks = new GradleTaskContainer();

	private final GradleSnippetContainer snippets = new GradleSnippetContainer();

	private final GradleBuildscript.Builder buildscript = new GradleBuildscript.Builder();

	private final GradleExtensionContainer extensions = new GradleExtensionContainer();

	public GradleBuildSettings.Builder getSettingsBuilder() {
		return this.settings;
	}

	public GradlePluginContainer getPluginContainer() {
		return this.plugins;
	}

	public GradleConfigurationContainer getConfigurationContainer() {
		return this.configurations;
	}

	public GradleTaskContainer getTaskContainer() {
		return this.tasks;
	}

	public GradleSnippetContainer getSnippetContainer() {
		return this.snippets;
	}

	public GradleBuildscript.Builder getBuildscriptBuilder() {
		return this.buildscript;
	}

	public GradleExtensionContainer getExtensionContainer() {
		return this.extensions;
	}

}
