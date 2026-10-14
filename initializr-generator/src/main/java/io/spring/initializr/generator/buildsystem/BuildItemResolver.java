/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import org.jspecify.annotations.Nullable;

/**
 * Resolve an item of the {@link Build} against an identifier.
 *
 * @author Stephane Nicoll
 */
public interface BuildItemResolver {

	/**
	 * A default {@link BuildItemResolver} that bypass resolution.
	 */
	SimpleBuildItemResolver NO_OP = new SimpleBuildItemResolver((id) -> null, (id) -> null, (id) -> null);

	/**
	 * Resolve the {@link Dependency} with the specified {@code id}.
	 * @param id the id of the dependency
	 * @return the matching {@link Dependency} or {@code null} if none is found
	 */
	@Nullable Dependency resolveDependency(String id);

	/**
	 * Resolve the {@link BillOfMaterials} with the specified {@code id}.
	 * @param id the id of the bom
	 * @return the matching {@link BillOfMaterials} or {@code null} if none is found
	 */
	@Nullable BillOfMaterials resolveBom(String id);

	/**
	 * Resolve the {@link MavenRepository repository} with the specified {@code id}.
	 * @param id the id of the bom
	 * @return the matching {@link MavenRepository} or {@code null} if none is found
	 */
	@Nullable MavenRepository resolveRepository(String id);

}
