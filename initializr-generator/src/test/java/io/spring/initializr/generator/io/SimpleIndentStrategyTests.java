/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link SimpleIndentStrategy}.
 *
 * @author Stephane Nicoll
 */
class SimpleIndentStrategyTests {

	@Test
	void noLevelIsAllowed() {
		assertThat(new SimpleIndentStrategy("  ").apply(0)).isEqualTo("");
	}

	@Test
	void singleLevelIndentSpace() {
		assertThat(new SimpleIndentStrategy("  ").apply(1)).isEqualTo("  ");
	}

	@Test
	void singleLevelIndentTab() {
		assertThat(new SimpleIndentStrategy("\t").apply(1)).isEqualTo("\t");
	}

	@Test
	void multiLevelIndentSpace() {
		assertThat(new SimpleIndentStrategy("  ").apply(3)).isEqualTo("      ");
	}

	@Test
	void multiLevelIndentTab() {
		assertThat(new SimpleIndentStrategy("\t").apply(3)).isEqualTo("\t\t\t");
	}

	@Test
	@SuppressWarnings("NullAway") // Test null checks
	void mustHaveIndent() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new SimpleIndentStrategy(null));
	}

	@Test
	void indentLevelMustNotBeNegative() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new SimpleIndentStrategy(" ").apply(-1))
			.withMessageContaining("Indent level must not be negative")
			.withMessageContaining("-1");
	}

}
