/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleConfigurationContainer}.
 *
 * @author Stephane Nicoll
 */
class GradleConfigurationContainerTests {

	@Test
	void isEmptyWithEmptyContainer() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithRegisteredName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void isEmptyWithCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingRegisteredName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		assertThat(container.has("devOnly")).isTrue();
	}

	@Test
	void hasWithMatchingCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.has("runtime")).isTrue();
	}

	@Test
	void hasWithNonMatchingNameOrCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.has("test")).isFalse();
	}

	@Test
	void removeWithMatchingName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("com.example");
		assertThat(container.remove("com.example")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithMatchingCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.remove("runtime")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingNameOrCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.remove("test")).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

}
