/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class TypeTests {

	@Test
	void parseAction() {
		Type type = new Type();
		type.setId("foo");
		type.setAction("my-action.zip");
		assertThat(type.getAction()).isEqualTo("/my-action.zip");
	}

}
