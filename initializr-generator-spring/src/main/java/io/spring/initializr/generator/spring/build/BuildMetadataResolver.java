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

package io.spring.initializr.generator.spring.build;

import java.util.Objects;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Resolve metadata information from the build.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 * @author Sijun Yang
 */
public class BuildMetadataResolver {

	private final InitializrMetadata metadata;

	private final Version platformVersion;

	public BuildMetadataResolver(InitializrMetadata metadata, Version platformVersion) {
		this.metadata = metadata;
		this.platformVersion = platformVersion;
	}

	/**
	 * Return a {@link Stream} of {@link Dependency dependency metadata} that are defined
	 * by the specified {@link Build}. If a dependency has no metadata it is skipped.
	 * @param build the build to query
	 * @return a stream of dependency metadata
	 */
	public Stream<Dependency> dependencies(Build build) {
		return build.dependencies()
			.ids()
			.map((id) -> this.metadata.getDependencies().get(id))
			.filter(Objects::nonNull)
			.map((dependency) -> dependency.resolve(this.platformVersion));
	}

	/**
	 * Specify if the given {@link Build} has the given {@code facet} enabled.
	 * @param build the build to query
	 * @param facet the facet to query
	 * @return {@code true} if this build defines at least a dependency with that facet
	 */
	public boolean hasFacet(Build build, String facet) {
		return dependencies(build).anyMatch((dependency) -> dependency.getFacets().contains(facet));
	}

	/**
	 * Checks if the given {@link Build} contains dependencies with the given
	 * {@code groupId}.
	 * @param build the build to query
	 * @param groupId the groupId to query
	 * @return {@code true} if this build defines at least a dependency with that groupId,
	 * {@code false} otherwise
	 */
	public boolean hasGroupId(Build build, String groupId) {
		return dependencies(build).anyMatch((dependency) -> dependency.getGroupId().equals(groupId));
	}

}
