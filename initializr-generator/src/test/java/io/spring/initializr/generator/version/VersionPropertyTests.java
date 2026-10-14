/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.version;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link VersionProperty}.
 *
 * @author Stephane Nicoll
 */
class VersionPropertyTests {

	@Test
	void testStandardProperty() {
		assertThat(VersionProperty.of("spring-boot.version").toStandardFormat()).isEqualTo("spring-boot.version");
	}

	@Test
	void testCamelCaseProperty() {
		assertThat(VersionProperty.of("spring-boot.version").toCamelCaseFormat()).isEqualTo("springBootVersion");
	}

	@Test
	void testStandardPropertyWithNoSeparator() {
		assertThat(VersionProperty.of("springbootversion").toStandardFormat()).isEqualTo("springbootversion");
	}

	@Test
	void testCamelCasePropertyWithNoSeparator() {
		assertThat(VersionProperty.of("springbootversion").toCamelCaseFormat()).isEqualTo("springbootversion");
	}

	@Test
	void testInvalidPropertyUpperCase() {
		assertThatIllegalArgumentException().isThrownBy(() -> VersionProperty.of("Spring-boot.version"));
	}

	@Test
	void testInvalidPropertyIllegalCharacter() {
		assertThatIllegalArgumentException().isThrownBy(() -> VersionProperty.of("spring-boot_version"))
			.withMessageContaining("Unsupported character");
	}

}
