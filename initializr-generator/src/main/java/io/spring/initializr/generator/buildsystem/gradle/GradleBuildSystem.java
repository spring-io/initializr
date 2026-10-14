/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.BuildSystem;

/**
 * Gradle {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
public final class GradleBuildSystem implements BuildSystem {

	/**
	 * Gradle {@link BuildSystem} identifier.
	 */
	public static final String ID = "gradle";

	/**
	 * Gradle build using the Groovy DSL.
	 */
	public static final String DIALECT_GROOVY = "groovy";

	/**
	 * Gradle build using the Kotlin DSL.
	 */
	public static final String DIALECT_KOTLIN = "kotlin";

	private final String dialect;

	public GradleBuildSystem() {
		this(DIALECT_GROOVY);
	}

	public GradleBuildSystem(String dialect) {
		this.dialect = dialect;
	}

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String dialect() {
		return this.dialect;
	}

	@Override
	public String toString() {
		return id();
	}

}
