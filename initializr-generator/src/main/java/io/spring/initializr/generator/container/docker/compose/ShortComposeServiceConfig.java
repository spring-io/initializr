/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.container.docker.compose;

/**
 * Docker Compose service config in short syntax. See
 * <a href="https://docs.docker.com/reference/compose-file/services/#short-syntax">the
 * reference documentation</a>.
 *
 * @param id the id
 * @author Moritz Halbritter
 */
record ShortComposeServiceConfig(String id) implements ComposeServiceConfig {
}
