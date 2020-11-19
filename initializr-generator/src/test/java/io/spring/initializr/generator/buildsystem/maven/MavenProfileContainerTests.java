/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
