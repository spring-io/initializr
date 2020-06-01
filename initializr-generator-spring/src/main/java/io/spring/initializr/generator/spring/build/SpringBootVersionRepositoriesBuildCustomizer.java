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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.version.Version;

/**
 * A {@link BuildCustomizer} that configures the build's repositories based on the version
 * of Spring Boot being used.
 *
 * @author Andy Wilkinson
 */
class SpringBootVersionRepositoriesBuildCustomizer implements BuildCustomizer<Build> {

	private static final MavenRepository SPRING_MILESTONES = MavenRepository
			.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone").name("Spring Milestones").build();

	private static final MavenRepository SPRING_SNAPSHOTS = MavenRepository
			.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot").name("Spring Snapshots")
			.snapshotsEnabled(true).build();

	private final Version springBootVersion;

	SpringBootVersionRepositoriesBuildCustomizer(Version springBootVersion) {
		this.springBootVersion = springBootVersion;
	}

	@Override
	public void customize(Build build) {
		build.repositories().add("maven-central");
		if (this.springBootVersion.getQualifier() != null) {
			String qualifier = this.springBootVersion.getQualifier().getId();
			if (!qualifier.equals("RELEASE")) {
				addMilestoneRepository(build);
				if (qualifier.contains("SNAPSHOT")) {
					addSnapshotRepository(build);
				}
			}
		}
	}

	private void addSnapshotRepository(Build build) {
		build.repositories().add(SPRING_SNAPSHOTS);
		build.pluginRepositories().add(SPRING_SNAPSHOTS);
	}

	private void addMilestoneRepository(Build build) {
		build.repositories().add(SPRING_MILESTONES);
		build.pluginRepositories().add(SPRING_MILESTONES);
	}

}
