/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.language;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TypeDeclaration}.
 *
 * @author Moritz Halbritter
 */
class TypeDeclarationTests {

	@Test
	void implementWithVarArgs() {
		TypeDeclaration declaration = new TypeDeclaration("Test");
		declaration.implement("Interface1", "Interface2");
		assertThat(declaration.getImplements()).containsExactly("Interface1", "Interface2");
	}

}
