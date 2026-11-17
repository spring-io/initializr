/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.container.docker.compose;

import org.jspecify.annotations.Nullable;

/**
 * Docker Compose service config in long syntax. See
 * <a href="https://docs.docker.com/reference/compose-file/services/#long-syntax">the
 * reference documentation</a>.
 *
 * @param source the source
 * @param target the optional target
 * @param mode the optional mode
 * @param uid the optional uid
 * @param gid the optional gid
 * @author Moritz Halbritter
 */
record LongComposeServiceConfig(String source, @Nullable String target, @Nullable Integer mode, @Nullable Integer uid,
		@Nullable Integer gid) implements ComposeServiceConfig {
	@Override
	public String id() {
		return this.source;
	}
}
