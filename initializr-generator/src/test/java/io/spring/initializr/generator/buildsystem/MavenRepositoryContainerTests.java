/*
 * Copyright 2012 - present the original author or authors.
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
		MavenRepository instance = MavenRepository.withIdAndUrl("test", "https://example.com/releases")
			.name("my repo")
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
		container.add(MavenRepository.withIdAndUrl("custom", "https://example.com/snapshots")
			.name("custom-snapshots")
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
