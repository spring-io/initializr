/*
 * Copyright 2012 - present the original author or authors.
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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

/**
 * A {@link BuildCustomizer} that configures the build's repositories based on the version
 * of Spring Boot being used.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
class SpringBootVersionRepositoriesBuildCustomizer implements BuildCustomizer<Build> {

	static final MavenRepository SPRING_MILESTONES = MavenRepository
		.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone")
		.name("Spring Milestones")
		.onlyReleases()
		.build();

	static final MavenRepository SPRING_SNAPSHOTS = MavenRepository
		.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot")
		.name("Spring Snapshots")
		.onlySnapshots()
		.build();

	private static final VersionRange SPRING_BOOT_4_0_OR_LATER = VersionParser.DEFAULT.parseRange("4.0.0-M1");

	private final Version springBootVersion;

	SpringBootVersionRepositoriesBuildCustomizer(Version springBootVersion) {
		this.springBootVersion = springBootVersion;
	}

	@Override
	public void customize(Build build) {
		build.repositories().add("maven-central");
		switch (getReleaseType()) {
			case MILESTONE -> addMilestoneRepositoryIfNeeded(build);
			case SNAPSHOT -> {
				if (isMaintenanceRelease()) {
					addSnapshotRepository(build);
				}
				else {
					addMilestoneRepositoryIfNeeded(build);
					addSnapshotRepository(build);
				}
			}
		}
	}

	private ReleaseType getReleaseType() {
		Version.Qualifier qualifier = this.springBootVersion.getQualifier();
		if (qualifier == null) {
			return ReleaseType.GA;
		}
		String id = qualifier.getId();
		if ("RELEASE".equals(id)) {
			return ReleaseType.GA;
		}
		if (id.contains("SNAPSHOT")) {
			return ReleaseType.SNAPSHOT;
		}
		return ReleaseType.MILESTONE;
	}

	private boolean isMaintenanceRelease() {
		Integer patch = this.springBootVersion.getPatch();
		return patch != null && patch > 0;
	}

	private void addSnapshotRepository(Build build) {
		build.repositories().add(SPRING_SNAPSHOTS);
		build.pluginRepositories().add(SPRING_SNAPSHOTS);
	}

	private void addMilestoneRepositoryIfNeeded(Build build) {
		if (SPRING_BOOT_4_0_OR_LATER.match(this.springBootVersion)) {
			// Spring Boot 4.0 and up publishes milestones to Maven Central
			return;
		}
		build.repositories().add(SPRING_MILESTONES);
		build.pluginRepositories().add(SPRING_MILESTONES);
	}

	private enum ReleaseType {

		GA, MILESTONE, SNAPSHOT

	}

}
