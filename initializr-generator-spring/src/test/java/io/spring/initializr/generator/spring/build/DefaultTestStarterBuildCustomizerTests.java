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
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultTestStarterBuildCustomizer}.
 *
 * @author Moritz Halbritter
 */
class DefaultTestStarterBuildCustomizerTests {

	@Test
	void defaultTestStarterIsAddedOnEmptyBuild() {
		Build build = new MavenBuild();
		new DefaultTestStarterBuildCustomizer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly(DefaultTestStarterBuildCustomizer.STARTER_ID);
	}

	@Test
	void defaultTestStarterIsAddedIfNoneExists() {
		Build build = new MavenBuild();
		build.dependencies().add("acme", "com.example", "acme", DependencyScope.COMPILE);
		new DefaultTestStarterBuildCustomizer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("acme", DefaultTestStarterBuildCustomizer.STARTER_ID);
	}

	@Test
	void defaultTestStarterIsAddedIfNoTestScopedStarterExists() {
		Build build = new MavenBuild();
		build.dependencies()
			.add("kafka-test", "org.springframework.boot", "spring-boot-starter-kafka-test", DependencyScope.COMPILE);
		new DefaultTestStarterBuildCustomizer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kafka-test", DefaultTestStarterBuildCustomizer.STARTER_ID);
	}

	@Test
	void defaultTestStarterIsNotAddedIfTestScopedStarterExists() {
		Build build = new MavenBuild();
		build.dependencies()
			.add("kafka-test", "org.springframework.boot", "spring-boot-starter-kafka-test",
					DependencyScope.TEST_COMPILE);
		new DefaultTestStarterBuildCustomizer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kafka-test");
	}

	@Test
	void defaultTestStarterIsNotAddedIfDefaultTestScopedStarterExists() {
		Build build = new MavenBuild();
		build.dependencies()
			.add("test", "org.springframework.boot", "spring-boot-starter-test", DependencyScope.TEST_COMPILE);
		new DefaultTestStarterBuildCustomizer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("test");
	}

}
