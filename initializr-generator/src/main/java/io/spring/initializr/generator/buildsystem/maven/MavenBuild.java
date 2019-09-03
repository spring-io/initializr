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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSettings.Builder;

/**
 * Maven build for a project.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MavenBuild extends Build {

	private final MavenBuildSettings.Builder settings = new Builder();

	private final MavenResourceContainer resources = new MavenResourceContainer();

	private final MavenResourceContainer testResources = new MavenResourceContainer();

	private MavenPluginContainer plugins = new MavenPluginContainer();

	public MavenBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
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

	public MavenResourceContainer resources() {
		return this.resources;
	}

	public MavenResourceContainer testResources() {
		return this.testResources;
	}

	public MavenPluginContainer plugins() {
		return this.plugins;
	}

}
