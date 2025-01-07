/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.versionresolver;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link DefaultMavenVersionResolver}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class DefaultMavenVersionResolverTests {

	private MavenVersionResolver resolver;

	@BeforeEach
	void createResolver(@TempDir Path temp) {
		this.resolver = new DefaultMavenVersionResolver(temp);
	}

	@Test
	void resolveDependenciesForSpringBoot() {
		Map<String, String> versions = this.resolver.resolveDependencies("org.springframework.boot",
				"spring-boot-dependencies", "3.4.1");
		assertThat(versions).containsEntry("io.micrometer:micrometer-core", "1.14.2")
			.containsEntry("org.springframework.boot:spring-boot-autoconfigure", "3.4.1")
			.containsEntry("org.junit.jupiter:junit-jupiter-api", "5.11.4");
	}

	@Test
	void resolveDependenciesForSpringCloud() {
		Map<String, String> versions = this.resolver.resolveDependencies("org.springframework.cloud",
				"spring-cloud-dependencies", "2024.0.0");
		assertThat(versions).containsEntry("com.netflix.eureka:eureka-client", "2.0.4");
	}

	@Test
	void resolveDependenciesUsingMilestones() {
		Map<String, String> versions = this.resolver.resolveDependencies("org.springframework.boot",
				"spring-boot-dependencies", "3.4.0-M1");
		assertThat(versions).containsEntry("org.flywaydb:flyway-core", "10.15.2");
	}

	@Test
	void resolveDependenciesUsingSnapshots() {
		Map<String, String> versions = this.resolver.resolveDependencies("org.springframework.boot",
				"spring-boot-dependencies", "3.4.0-SNAPSHOT");
		assertThat(versions).isNotEmpty();
	}

	@Test
	void resolveDependenciesForNonExistentDependency() {
		assertThatIllegalStateException()
			.isThrownBy(() -> this.resolver.resolveDependencies("org.springframework.boot", "spring-boot-bom", "1.0"))
			.withMessage("Bom 'org.springframework.boot:spring-boot-bom:1.0' could not be resolved");
	}

	@Test
	void resolvePluginsForSpringBoot() {
		Map<String, String> versions = this.resolver.resolvePlugins("org.springframework.boot",
				"spring-boot-starter-parent", "3.4.1");
		assertThat(versions).containsEntry("org.springframework.boot:spring-boot-maven-plugin", "3.4.1");
	}

	@Test
	void resolvePluginsUsingMilestones() {
		Map<String, String> versions = this.resolver.resolvePlugins("org.springframework.boot",
				"spring-boot-dependencies", "3.4.0-M1");
		assertThat(versions).containsEntry("org.springframework.boot:spring-boot-maven-plugin", "3.4.0-M1");
	}

	@Test
	void resolvePluginsUsingMilestoneThatHasResolutionProblem() {
		Map<String, String> versions = this.resolver.resolvePlugins("org.springframework.boot",
				"spring-boot-dependencies", "3.0.0-M1");
		assertThat(versions).containsEntry("org.springframework.boot:spring-boot-maven-plugin", "3.0.0-M1");
	}

	@Test
	void resolvePluginsUsingSnapshots() {
		Map<String, String> versions = this.resolver.resolvePlugins("org.springframework.boot",
				"spring-boot-dependencies", "3.4.0-SNAPSHOT");
		assertThat(versions).isNotEmpty();
	}

	@Test
	void resolvePluginsForNonExistentDependency() {
		assertThatIllegalStateException()
			.isThrownBy(() -> this.resolver.resolvePlugins("org.springframework.boot", "spring-boot-bom", "1.0"))
			.withMessage("Pom 'org.springframework.boot:spring-boot-bom:1.0' could not be resolved");
	}

}
