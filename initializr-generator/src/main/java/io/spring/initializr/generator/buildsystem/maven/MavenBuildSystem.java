/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.BuildSystem;

/**
 * Maven {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
public final class MavenBuildSystem implements BuildSystem {

	/**
	 * Maven {@link BuildSystem} identifier.
	 */
	public static final String ID = "maven";

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String toString() {
		return id();
	}

}
