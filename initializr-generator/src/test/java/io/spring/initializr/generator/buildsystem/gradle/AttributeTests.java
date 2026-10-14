/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Attribute}.
 *
 * @author Moritz Halbritter
 */
class AttributeTests {

	@Test
	void attributeSetHasSensibleToString() {
		assertThat(Attribute.set("name", "value")).hasToString("name = value");
	}

	@Test
	void attributeAppendHasSensibleToString() {
		assertThat(Attribute.append("name", "value")).hasToString("name += value");
	}

}
