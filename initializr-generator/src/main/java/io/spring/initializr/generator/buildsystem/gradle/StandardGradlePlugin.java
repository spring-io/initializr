/*
 * Copyright 2012 - present the original author or authors.
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
