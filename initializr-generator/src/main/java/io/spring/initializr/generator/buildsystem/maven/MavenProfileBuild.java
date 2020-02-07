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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MavenProfileBuild {

	private final String defaultGoal;

	private final String directory;

	private final String finalName;

	private final List<String> filters;

	private final MavenResourceContainer resources;

	private final MavenResourceContainer testResources;

	private final MavenPluginManagement pluginManagement;

	private final MavenPluginContainer plugins;

	protected MavenProfileBuild(Builder builder) {
		this.defaultGoal = builder.defaultGoal;
		this.directory = builder.directory;
		this.finalName = builder.finalName;
		this.filters = Optional.ofNullable(builder.filters).map(Collections::unmodifiableList).orElse(null);
		this.resources = builder.resources;
		this.testResources = builder.testResources;
		this.pluginManagement = Optional.ofNullable(builder.pluginManagementBuilder)
				.map(MavenPluginManagement.Builder::build).orElse(null);
		this.plugins = builder.plugins;
	}

	public String getDefaultGoal() {
		return this.defaultGoal;
	}

	public String getDirectory() {
		return this.directory;
	}

	public String getFinalName() {
		return this.finalName;
	}

	public List<String> getFilters() {
		return this.filters;
	}

	public MavenResourceContainer getResources() {
		return this.resources;
	}

	public MavenResourceContainer getTestResources() {
		return this.testResources;
	}

	public MavenPluginManagement getPluginManagement() {
		return this.pluginManagement;
	}

	public MavenPluginContainer getPlugins() {
		return this.plugins;
	}

	public static class Builder {

		private String defaultGoal;

		private String directory;

		private String finalName;

		private List<String> filters;

		private MavenResourceContainer resources;

		private MavenResourceContainer testResources;

		private MavenPluginManagement.Builder pluginManagementBuilder;

		private MavenPluginContainer plugins;

		protected Builder() {
			this.resources = new MavenResourceContainer();
			this.testResources = new MavenResourceContainer();
			this.plugins = new MavenPluginContainer();
		}

		public MavenProfileBuild.Builder defaultGoal(String defaultGoal) {
			this.defaultGoal = defaultGoal;
			return this;
		}

		public MavenProfileBuild.Builder directory(String directory) {
			this.directory = directory;
			return this;
		}

		public MavenProfileBuild.Builder finalName(String finalName) {
			this.finalName = finalName;
			return this;
		}

		public MavenProfileBuild.Builder filter(String filter) {
			if (this.filters == null) {
				this.filters = new LinkedList<>();
			}
			this.filters.add(filter);
			return this;
		}

		public MavenProfileBuild.Builder resources(Consumer<MavenResourceContainer> resources) {
			resources.accept(this.resources);
			return this;
		}

		public MavenProfileBuild.Builder testResources(Consumer<MavenResourceContainer> testResources) {
			testResources.accept(this.testResources);
			return this;
		}

		public MavenProfileBuild.Builder pluginManagement(Consumer<MavenPluginManagement.Builder> pluginManagement) {
			if (this.pluginManagementBuilder == null) {
				this.pluginManagementBuilder = new MavenPluginManagement.Builder();
			}
			pluginManagement.accept(this.pluginManagementBuilder);
			return this;
		}

		public MavenProfileBuild.Builder plugins(Consumer<MavenPluginContainer> plugins) {
			plugins.accept(this.plugins);
			return this;
		}

		public MavenProfileBuild build() {
			return new MavenProfileBuild(this);
		}

	}

}
