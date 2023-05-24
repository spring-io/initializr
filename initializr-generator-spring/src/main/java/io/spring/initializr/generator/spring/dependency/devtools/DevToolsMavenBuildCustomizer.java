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

package io.spring.initializr.generator.spring.dependency.devtools;

import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.maven.OptionalDependencyMavenBuildCustomizer;

/**
 * Maven {@link BuildCustomizer} that sets the "optional" flag when devtools is selected.
 *
 * @author Stephane Nicoll
 * @deprecated in favor of {@link OptionalDependencyMavenBuildCustomizer}
 */
@Deprecated(since = "0.20.0", forRemoval = true)
public class DevToolsMavenBuildCustomizer extends OptionalDependencyMavenBuildCustomizer {

	/**
	 * Create a new instance with the identifier for the devtools dependency.
	 * @param devtoolsDependencyId the id of the devtools dependency
	 */
	public DevToolsMavenBuildCustomizer(String devtoolsDependencyId) {
		super(devtoolsDependencyId);
	}

}
