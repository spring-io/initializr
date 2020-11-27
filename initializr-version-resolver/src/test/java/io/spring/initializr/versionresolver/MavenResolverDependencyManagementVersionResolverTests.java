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

package io.spring.initializr.versionresolver;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link MavenResolverDependencyManagementVersionResolver}.
 *
 * @author Andy Wilkinson
 */
class MavenResolverDependencyManagementVersionResolverTests {

	private DependencyManagementVersionResolver resolver;

	@BeforeEach
	void createResolver(@TempDir Path temp) {
		this.resolver = new MavenResolverDependencyManagementVersionResolver(temp);
	}

	@Test
	void springBootDependencies() {
		Map<String, String> versions = this.resolver.resolve("org.springframework.boot", "spring-boot-dependencies",
				"2.1.5.RELEASE");
		assertThat(versions).containsEntry("org.flywaydb:flyway-core", "5.2.4");
	}

	@Test
	void springCloudDependencies() {
		Map<String, String> versions = this.resolver.resolve("org.springframework.cloud", "spring-cloud-dependencies",
				"Greenwich.SR1");
		assertThat(versions).containsEntry("com.netflix.ribbon:ribbon", "2.3.0");
	}

	@Test
	void milestoneBomCanBeResolved() {
		Map<String, String> versions = this.resolver.resolve("org.springframework.boot", "spring-boot-dependencies",
				"2.2.0.M3");
		assertThat(versions).containsEntry("org.flywaydb:flyway-core", "5.2.4");
	}

	@Test
	void snapshotBomCanBeResolved() {
		Map<String, String> versions = this.resolver.resolve("org.springframework.boot", "spring-boot-dependencies",
				"2.4.0-SNAPSHOT");
		assertThat(versions).isNotEmpty();
	}

	@Test
	void nonExistentDependency() {
		assertThatIllegalStateException()
				.isThrownBy(() -> this.resolver.resolve("org.springframework.boot", "spring-boot-bom", "1.0"))
				.withMessage("Bom 'org.springframework.boot:spring-boot-bom:1.0' could not be resolved");
	}

}
