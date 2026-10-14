/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import org.jspecify.annotations.Nullable;

/**
 * A factory for creating a {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
public interface BuildSystemFactory {

	/**
	 * Creates and returns a {@link BuildSystem} for the given id. If the factory does not
	 * recognise the given {@code id}, {@code null} should be returned.
	 * @param id the id of the build system
	 * @return the build system or {@code null}
	 */
	@Nullable BuildSystem createBuildSystem(@Nullable String id);

	/**
	 * Creates and returns a {@link BuildSystem} for the given {@code id} and
	 * {@code dialect}. If the factory does not recognize the given {@code id} or
	 * {@code dialect}, {@code null} should be returned.
	 * @param id the id of the build system
	 * @param dialect the dialect of the build system
	 * @return the build system or {@code null}
	 */
	default @Nullable BuildSystem createBuildSystem(@Nullable String id, @Nullable String dialect) {
		return createBuildSystem(id);
	}

}
