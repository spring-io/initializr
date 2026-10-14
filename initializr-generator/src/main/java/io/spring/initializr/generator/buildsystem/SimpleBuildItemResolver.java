/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.util.function.Function;

import org.jspecify.annotations.Nullable;

/**
 * A simple {@link BuildItemResolver} implementation.
 *
 * @author Stephane Nicoll
 */
public class SimpleBuildItemResolver implements BuildItemResolver {

	private final Function<String, @Nullable Dependency> dependencyResolver;

	private final Function<String, @Nullable BillOfMaterials> bomResolver;

	private final Function<String, @Nullable MavenRepository> repositoryResolver;

	public SimpleBuildItemResolver(Function<String, @Nullable Dependency> dependencyResolver,
			Function<String, @Nullable BillOfMaterials> bomResolver,
			Function<String, @Nullable MavenRepository> repositoryResolver) {
		this.dependencyResolver = dependencyResolver;
		this.bomResolver = bomResolver;
		this.repositoryResolver = repositoryResolver;
	}

	@Override
	public @Nullable Dependency resolveDependency(String id) {
		return this.dependencyResolver.apply(id);
	}

	@Override
	public @Nullable BillOfMaterials resolveBom(String id) {
		return this.bomResolver.apply(id);
	}

	@Override
	public @Nullable MavenRepository resolveRepository(String id) {
		return this.repositoryResolver.apply(id);
	}

}
