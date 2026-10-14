/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SimpleKotlinProjectSettings}.
 *
 * @author Moritz Halbritter
 */
class SimpleKotlinProjectSettingsTests {

	@ParameterizedTest
	@ValueSource(strings = { "2.1.0", "2.4.0" })
	void shouldUseDefaultFlags(String kotlinVersion) {
		SimpleKotlinProjectSettings settings = new SimpleKotlinProjectSettings(kotlinVersion, "17");
		assertThat(settings.getCompilerArgs()).containsExactly("-Xjsr305=strict");
	}

	@ParameterizedTest
	@ValueSource(strings = { "2.2.0", "2.3.0" })
	void shouldUseAnnotationDefaultTargetWhenUsingKotlin22OrKotlin23(String kotlinVersion) {
		SimpleKotlinProjectSettings settings = new SimpleKotlinProjectSettings(kotlinVersion, "17");
		assertThat(settings.getCompilerArgs()).containsExactlyInAnyOrder("-Xjsr305=strict",
				"-Xannotation-default-target=param-property");
	}

}
