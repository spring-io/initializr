/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleTask.Attribute;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleTask}.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("removal")
class GradleTaskTests {

	@Test
	void attributeSetHasSensibleToString() {
		assertThat(Attribute.set("name", "value")).hasToString("name = value");
	}

	@Test
	void attributeAppendHasSensibleToString() {
		assertThat(Attribute.append("name", "value")).hasToString("name += value");
	}

}
