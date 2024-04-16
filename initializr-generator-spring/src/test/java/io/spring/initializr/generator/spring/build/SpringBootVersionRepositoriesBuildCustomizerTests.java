/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringBootVersionRepositoriesBuildCustomizer}.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
class SpringBootVersionRepositoriesBuildCustomizerTests {

	@Test
	void addMavenCentralWhenUsingRelease() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0.RELEASE")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL);
	}

	@Test
	void addMavenCentralWhenUsingSemVerRelease() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL);
	}

	@Test
	void addMavenCentralAndMilestonesWhenUsingMilestone() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0.M1")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES);
	}

	@Test
	void addMavenCentralAndMilestonesWhenUsingSemVerMilestone() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0-M1")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES);
	}

	@Test
	void addMavenCentralAndMilestonesWhenUsingReleaseCandidate() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0.RC1")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES);
	}

	@Test
	void addMavenCentralAndMilestonesWhenUsingSemVerReleaseCandidate() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0-RC1")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES);
	}

	@Test
	void addMavenCentralAndNonReleaseWhenUsingSnapshot() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0.BUILD-SNAPSHOT")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_SNAPSHOTS);
	}

	@Test
	void firstSnapshotReleaseShouldAddMilestoneRepository() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.0-SNAPSHOT")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_MILESTONES,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_SNAPSHOTS);
	}

	@Test
	void maintenanceReleasesShouldNotAddMilestoneRepository() {
		MavenBuild build = new MavenBuild();
		new SpringBootVersionRepositoriesBuildCustomizer(Version.parse("2.1.1-SNAPSHOT")).customize(build);
		assertThat(build.repositories().items()).containsExactly(MavenRepository.MAVEN_CENTRAL,
				SpringBootVersionRepositoriesBuildCustomizer.SPRING_SNAPSHOTS);
	}

}
