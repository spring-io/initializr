/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
