/*
 * Copyright 2012-2021 the original author or authors.
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

package io.spring.initializr.generator.buildsystem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenRepositoryContainer}.
 *
 * @author Stephane Nicoll
 */
class MavenRepositoryContainerTests {

	@Test
	void addMavenRepository() {
		MavenRepositoryContainer container = createTestContainer();
		container.add(MavenRepository.withIdAndUrl("test", "https://example.com/releases").name("my repo"));
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("test")).isTrue();
		MavenRepository repository = container.get("test");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("my repo");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/releases");
		assertThat(repository.isReleasesEnabled()).isTrue();
		assertThat(repository.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void addMavenRepositoryInstance() {
		MavenRepositoryContainer container = createTestContainer();
		MavenRepository instance = MavenRepository.withIdAndUrl("test", "https://example.com/releases").name("my repo")
				.build();
		container.add(instance);
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("test")).isTrue();
		MavenRepository repository = container.get("test");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("my repo");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/releases");
		assertThat(repository.isReleasesEnabled()).isTrue();
		assertThat(repository.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void addMavenRepositoryWithSnapshotsEnabled() {
		MavenRepositoryContainer container = createTestContainer();
		container.add(MavenRepository.withIdAndUrl("custom", "https://example.com/snapshots").name("custom-snapshots")
				.onlySnapshots());
		assertThat(container.ids()).containsOnly("custom");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("custom")).isTrue();
		MavenRepository repository = container.get("custom");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("custom-snapshots");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/snapshots");
		assertThat(repository.isReleasesEnabled()).isFalse();
		assertThat(repository.isSnapshotsEnabled()).isTrue();
	}

	private MavenRepositoryContainer createTestContainer() {
		return new MavenRepositoryContainer((id) -> null);
	}

}
