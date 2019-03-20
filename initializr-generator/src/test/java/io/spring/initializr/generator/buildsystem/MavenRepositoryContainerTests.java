/*
 * Copyright 2012-2019 the original author or authors.
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
		container.add("test", "my repo", "https://example.com/releases");
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("test")).isTrue();
		MavenRepository repository = container.get("test");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("my repo");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/releases");
		assertThat(repository.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void addMavenRepositoryInstance() {
		MavenRepositoryContainer container = createTestContainer();
		MavenRepository instance = new MavenRepository("test", "my repo",
				"https://example.com/releases");
		container.add(instance);
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("test")).isTrue();
		MavenRepository repository = container.get("test");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("my repo");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/releases");
		assertThat(repository.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void addMavenRepositoryWithSnapshotsEnabled() {
		MavenRepositoryContainer container = createTestContainer();
		container.add("custom", "custom-snapshots", "https://example.com/snapshots",
				true);
		assertThat(container.ids()).containsOnly("custom");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("custom")).isTrue();
		MavenRepository repository = container.get("custom");
		assertThat(repository).isNotNull();
		assertThat(repository.getName()).isEqualTo("custom-snapshots");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/snapshots");
		assertThat(repository.isSnapshotsEnabled()).isTrue();
	}

	private MavenRepositoryContainer createTestContainer() {
		return new MavenRepositoryContainer((id) -> null);
	}

}
