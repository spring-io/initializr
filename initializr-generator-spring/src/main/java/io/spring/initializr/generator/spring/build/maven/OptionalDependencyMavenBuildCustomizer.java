/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * Maven {@link BuildCustomizer} that sets the "optional" flag for a dependency.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class OptionalDependencyMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final String dependencyId;

	/**
	 * Create a new instance with the identifier for the dependency.
	 * @param dependencyId the id of the dependency
	 */
	public OptionalDependencyMavenBuildCustomizer(String dependencyId) {
		this.dependencyId = dependencyId;
	}

	@Override
	public void customize(MavenBuild build) {
		Dependency dependency = build.dependencies().get(this.dependencyId);
		if (dependency != null) {
			build.dependencies().add(this.dependencyId, MavenDependency.from(dependency).optional(true));
		}
	}

}
