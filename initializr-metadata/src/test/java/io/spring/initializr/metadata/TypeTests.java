/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
