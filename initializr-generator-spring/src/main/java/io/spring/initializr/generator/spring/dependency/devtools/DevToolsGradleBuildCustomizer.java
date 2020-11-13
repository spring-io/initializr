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

package io.spring.initializr.generator.spring.dependency.devtools;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

/**
 * Gradle {@link BuildCustomizer} that creates a dedicated "developmentOnly" configuration
 * when devtools is selected.
 *
 * @author Stephane Nicoll
 */
public class DevToolsGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private static final VersionRange SPRING_BOOT_2_3_0_RC1_OR_LATER = VersionParser.DEFAULT.parseRange("2.3.0.RC1");

	private final Version platformVersion;

	private final String devtoolsDependencyId;

	/**
	 * Create a new instance with the requested {@link Version platform version} and the
	 * identifier for the devtools dependency.
	 * @param platformVersion the version of the plateform
	 * @param devtoolsDependencyId the id of the devtools dependency
	 */
	public DevToolsGradleBuildCustomizer(Version platformVersion, String devtoolsDependencyId) {
		this.platformVersion = platformVersion;
		this.devtoolsDependencyId = devtoolsDependencyId;
	}

	@Override
	public void customize(GradleBuild build) {
		Dependency devtools = build.dependencies().get(this.devtoolsDependencyId);
		if (devtools == null) {
			return;
		}
		if (!SPRING_BOOT_2_3_0_RC1_OR_LATER.match(this.platformVersion)) {
			build.configurations().add("developmentOnly");
			build.configurations().customize("runtimeClasspath",
					(runtimeClasspath) -> runtimeClasspath.extendsFrom("developmentOnly"));
		}
		build.dependencies().add(this.devtoolsDependencyId,
				GradleDependency.from(devtools).configuration("developmentOnly"));
	}

}
