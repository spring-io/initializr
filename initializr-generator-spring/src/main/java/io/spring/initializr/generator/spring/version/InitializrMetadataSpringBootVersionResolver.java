/*
 * Copyright 2012-2022 the original author or authors.
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

package io.spring.initializr.generator.spring.version;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * {@link SpringBootVersionResolver} that resolves the version from
 * {@link InitializrMetadata} when not provided in the project description.
 *
 * @author Filip Hrisafov
 */
public class InitializrMetadataSpringBootVersionResolver implements SpringBootVersionResolver {

	private final InitializrMetadata metadata;

	public InitializrMetadataSpringBootVersionResolver(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public Version resolveSpringBootVersion(ProjectDescription projectDescription) {
		return (projectDescription.getPlatformVersion() != null) ? projectDescription.getPlatformVersion()
				: Version.parse(this.metadata.getBootVersions().getDefault().getId());
	}

}
