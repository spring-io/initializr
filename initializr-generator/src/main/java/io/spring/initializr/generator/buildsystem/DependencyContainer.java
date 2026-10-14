/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

/**
 * A {@link BuildItemContainer} implementation for dependencies.
 *
 * @author Stephane Nicoll
 */
public class DependencyContainer extends BuildItemContainer<String, Dependency> {

	/**
	 * Create an instance with the specified {@code itemResolver}.
	 * @param itemResolver the function that returns a {@link Dependency} based on an
	 * identifier.
	 */
	public DependencyContainer(Function<String, @Nullable Dependency> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register a {@link Dependency} with the specified {@code id} and a managed version.
	 * @param id the id of the dependency
	 * @param groupId the groupId
	 * @param artifactId the artifactId
	 * @param scope the {@link DependencyScope}
	 */
	public void add(String id, String groupId, String artifactId, DependencyScope scope) {
		add(id, Dependency.withCoordinates(groupId, artifactId).scope(scope));
	}

	/**
	 * Register a {@link Dependency} with the specified {@code id} and
	 * {@link Dependency.Builder state}.
	 * @param id the id of the dependency
	 * @param builder the state of the dependency
	 */
	public void add(String id, Dependency.Builder<?> builder) {
		add(id, builder.build());
	}

}
