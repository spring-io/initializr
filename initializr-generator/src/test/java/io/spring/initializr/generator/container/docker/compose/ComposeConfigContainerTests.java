/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.container.docker.compose;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ComposeConfigContainer}.
 *
 * @author Moritz Halbritter
 */
class ComposeConfigContainerTests {

	@Test
	void shouldNotAllowDuplicateNames() {
		ComposeConfigContainer container = new ComposeConfigContainer();
		container.add("config-1", ComposeConfig.Builder.forExternal().build());
		container.add("config-2", ComposeConfig.Builder.forExternal().build());
		assertThatIllegalStateException()
			.isThrownBy(() -> container.add("config-1", ComposeConfig.Builder.forExternal().build()));
	}

}
