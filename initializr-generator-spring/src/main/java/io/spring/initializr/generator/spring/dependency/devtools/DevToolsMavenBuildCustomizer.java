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

package io.spring.initializr.generator.spring.dependency.devtools;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * Maven {@link BuildCustomizer} that sets the "optional" flag when devtools is selected.
 *
 * @author Stephane Nicoll
 */
public class DevToolsMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final String devtoolsDependencyId;

	/**
	 * Create a new instance with the identifier for the devtools dependency.
	 * @param devtoolsDependencyId the id of the devtools dependency
	 */
	public DevToolsMavenBuildCustomizer(String devtoolsDependencyId) {
		this.devtoolsDependencyId = devtoolsDependencyId;
	}

	@Override
	public void customize(MavenBuild build) {
		Dependency devtools = build.dependencies().get(this.devtoolsDependencyId);
		if (devtools != null) {
			build.dependencies().add("devtools", MavenDependency.from(devtools).optional(true));
		}
	}

}
