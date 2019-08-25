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

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * A {@link BuildCustomizer} to configure the Spring Boot plugin and dependency management
 * feature for a {@link GradleBuild}.
 *
 * @author Stephane Nicoll
 */
public final class SpringBootPluginBuildCustomizer implements BuildCustomizer<GradleBuild> {

	/**
	 * Order of this customizer. Runs before default customizers so that these plugins are
	 * added at the beginning of the {@code plugins} block.
	 */
	public static final int ORDER = -100;

	private final ProjectDescription description;

	private final DependencyManagementPluginVersionResolver versionResolver;

	public SpringBootPluginBuildCustomizer(ProjectDescription description,
			DependencyManagementPluginVersionResolver versionResolver) {
		this.description = description;
		this.versionResolver = versionResolver;
	}

	@Override
	public void customize(GradleBuild build) {
		build.plugins().add("org.springframework.boot",
				(plugin) -> plugin.setVersion(this.description.getPlatformVersion().toString()));
		build.plugins().add("io.spring.dependency-management", (plugin) -> plugin
				.setVersion(this.versionResolver.resolveDependencyManagementPluginVersion(this.description)));
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

}
