/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
