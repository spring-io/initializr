/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenRepository}.
 *
 * @author Stephane Nicoll
 */
class MavenRepositoryTests {

	@Test
	void repositoryWithDetails() {
		MavenRepository repo = MavenRepository.withIdAndUrl("test", "https://repo.example.com")
			.name("Test repository")
			.build();
		assertThat(repo.getId()).isEqualTo("test");
		assertThat(repo.getUrl()).isEqualTo("https://repo.example.com");
		assertThat(repo.getName()).isEqualTo("Test repository");
	}

	@Test
	void repositoryByDefaultOnlyUseReleases() {
		MavenRepository repo = MavenRepository.withIdAndUrl("test", "https://repo.example.com").build();
		assertThat(repo.isReleasesEnabled()).isTrue();
		assertThat(repo.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void repositoryWithOnlyReleases() {
		MavenRepository repo = MavenRepository.withIdAndUrl("test", "https://repo.example.com").onlyReleases().build();
		assertThat(repo.isReleasesEnabled()).isTrue();
		assertThat(repo.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void repositoryWithOnlySnapshots() {
		MavenRepository repo = MavenRepository.withIdAndUrl("test", "https://repo.example.com").onlySnapshots().build();
		assertThat(repo.isReleasesEnabled()).isFalse();
		assertThat(repo.isSnapshotsEnabled()).isTrue();
	}

	@Test
	void repositoryWithReleasesAndSnapshots() {
		MavenRepository repo = MavenRepository.withIdAndUrl("test", "https://repo.example.com")
			.releasesEnabled(true)
			.snapshotsEnabled(true)
			.build();
		assertThat(repo.isReleasesEnabled()).isTrue();
		assertThat(repo.isSnapshotsEnabled()).isTrue();
	}

}
