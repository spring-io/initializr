/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenProfileContainer}.
 */
class MavenProfileContainerTests {

	@Test
	void profileWithSameIdReturnSameInstance() {
		MavenProfileContainer container = createTestContainer();
		MavenProfile profile = container.id("profile1");
		assertThat(container.id("profile1")).isSameAs(profile);
	}

	@Test
	void isEmptyWithEmptyContainer() {
		MavenProfileContainer container = createTestContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithRegisteredProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void idsWithEmptyContainer() {
		MavenProfileContainer container = createTestContainer();
		assertThat(container.ids()).isEmpty();
	}

	@Test
	void idsWithRegisteredProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.ids()).containsOnly("profile1");
	}

	@Test
	void hasProfileWithMatchingProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.has("profile1")).isTrue();
	}

	@Test
	void hasProfileWithNonMatchingProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.has("profile2")).isFalse();
	}

	@Test
	void removeWithMatchingProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.remove("profile1")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingProfile() {
		MavenProfileContainer container = createTestContainer();
		container.id("profile1");
		assertThat(container.remove("profile2")).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	private MavenProfileContainer createTestContainer() {
		return new MavenProfileContainer(BuildItemResolver.NO_OP);
	}

}
