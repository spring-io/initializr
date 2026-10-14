/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.container.docker.compose;

import org.jspecify.annotations.Nullable;

/**
 * A configuration for a Docker Compose service.
 *
 * @author Moritz Halbritter
 */
public sealed interface ComposeServiceConfig extends Comparable<ComposeServiceConfig>
		permits ShortComposeServiceConfig, LongComposeServiceConfig {

	/**
	 * Id of the element.
	 * @return the id of the element.
	 */
	String id();

	@Override
	default int compareTo(ComposeServiceConfig o) {
		return id().compareTo(o.id());
	}

	/**
	 * Creates a config in short syntax. See
	 * <a href="https://docs.docker.com/reference/compose-file/services/#short-syntax">the
	 * reference documentation</a>.
	 * @param id the id of the config
	 * @return the config
	 */
	static ComposeServiceConfig ofShort(String id) {
		return new ShortComposeServiceConfig(id);
	}

	/**
	 * Creates a config in long syntax. See
	 * <a href="https://docs.docker.com/reference/compose-file/services/#long-syntax">the
	 * reference documentation</a>.
	 * @param source the source of the config
	 * @return the config
	 */
	static ComposeServiceConfig ofLong(String source) {
		return ofLong(source, null, null, null, null);
	}

	/**
	 * Creates a config in long syntax. See
	 * <a href="https://docs.docker.com/reference/compose-file/services/#long-syntax">the
	 * reference documentation</a>.
	 * @param source the source of the config
	 * @param target the target of the config
	 * @return the config
	 */
	static ComposeServiceConfig ofLong(String source, @Nullable String target) {
		return ofLong(source, target, null, null, null);
	}

	/**
	 * Creates a config in long syntax. See
	 * <a href="https://docs.docker.com/reference/compose-file/services/#long-syntax">the
	 * reference documentation</a>.
	 * @param source the source of the config
	 * @param target the target of the config
	 * @param mode the mode of the config
	 * @return the config
	 */
	static ComposeServiceConfig ofLong(String source, @Nullable String target, @Nullable Integer mode) {
		return ofLong(source, target, mode, null, null);
	}

	/**
	 * Creates a config in long syntax. See
	 * <a href="https://docs.docker.com/reference/compose-file/services/#long-syntax">the
	 * reference documentation</a>.
	 * @param source the source of the config
	 * @param target the target of the config
	 * @param mode the mode of the config
	 * @param uid the uid of the config
	 * @param gid the gid of the config
	 * @return the config
	 */
	static ComposeServiceConfig ofLong(String source, @Nullable String target, @Nullable Integer mode,
			@Nullable Integer uid, @Nullable Integer gid) {
		return new LongComposeServiceConfig(source, target, mode, uid, gid);
	}

}
