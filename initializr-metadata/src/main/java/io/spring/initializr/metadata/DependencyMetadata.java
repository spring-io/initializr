/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.util.Map;

import io.spring.initializr.generator.version.Version;
import org.jspecify.annotations.Nullable;

/**
 * Dependency metadata for a given spring boot {@link Version}.
 *
 * @author Stephane Nicoll
 */
public class DependencyMetadata {

	final @Nullable Version bootVersion;

	final @Nullable Map<String, Dependency> dependencies;

	final @Nullable Map<String, Repository> repositories;

	final @Nullable Map<String, BillOfMaterials> boms;

	public DependencyMetadata() {
		this(null, null, null, null);
	}

	public DependencyMetadata(@Nullable Version bootVersion, @Nullable Map<String, Dependency> dependencies,
			@Nullable Map<String, Repository> repositories, @Nullable Map<String, BillOfMaterials> boms) {
		this.bootVersion = bootVersion;
		this.dependencies = dependencies;
		this.repositories = repositories;
		this.boms = boms;
	}

	public @Nullable Version getBootVersion() {
		return this.bootVersion;
	}

	public @Nullable Map<String, Dependency> getDependencies() {
		return this.dependencies;
	}

	public @Nullable Map<String, Repository> getRepositories() {
		return this.repositories;
	}

	public @Nullable Map<String, BillOfMaterials> getBoms() {
		return this.boms;
	}

}
