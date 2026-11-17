/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.BuildSystemFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link BuildSystemFactory Factory} for {@link MavenBuildSystem}.
 *
 * @author Andy Wilkinson
 */
class MavenBuildSystemFactory implements BuildSystemFactory {

	@Override
	public @Nullable MavenBuildSystem createBuildSystem(@Nullable String id) {
		if (MavenBuildSystem.ID.equals(id)) {
			return new MavenBuildSystem();
		}
		return null;
	}

}
