/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import org.jspecify.annotations.Nullable;

/**
 * A standard {@link GradlePlugin} used in the {@code plugins} block.
 *
 * @author HaiTao Zhang
 */
public class StandardGradlePlugin extends GradlePlugin {

	private @Nullable String version;

	public StandardGradlePlugin(String id) {
		super(id, false);
	}

	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	public @Nullable String getVersion() {
		return this.version;
	}

}
