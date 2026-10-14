/*
 * Copyright 2012 - present the original author or authors.
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
