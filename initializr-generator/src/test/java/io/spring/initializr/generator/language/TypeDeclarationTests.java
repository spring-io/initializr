/*
 * Copyright 2012 - present the original author or authors.
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
