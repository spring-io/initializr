/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.BuildSystemFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link BuildSystemFactory Factory} for {@link GradleBuildSystem}.
 *
 * @author Andy Wilkinson
 */
class GradleBuildSystemFactory implements BuildSystemFactory {

	@Override
	public @Nullable BuildSystem createBuildSystem(@Nullable String id) {
		return createBuildSystem(id, null);
	}

	@Override
	public @Nullable BuildSystem createBuildSystem(@Nullable String id, @Nullable String dialect) {
		if (GradleBuildSystem.ID.equals(id)) {
			if (dialect == null) {
				return new GradleBuildSystem();
			}
			if (dialect.equals(GradleBuildSystem.DIALECT_GROOVY) || dialect.equals(GradleBuildSystem.DIALECT_KOTLIN)) {
				return new GradleBuildSystem(dialect);
			}
		}
		return null;
	}

}
