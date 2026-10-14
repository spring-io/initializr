/*
 * Copyright 2012 - present the original author or authors.
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

}
