/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleConfigurationBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class GradleConfigurationBuildCustomizerTests {

	@Test
	void providedRuntimeConfigurationIsAddedWithNonWarProject() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("servlet", "jakarta.servlet", "jakarta.servlet-api", DependencyScope.PROVIDED_RUNTIME);
		customize(build);
		assertThat(build.configurations().names()).containsOnly("providedRuntime");
	}

	@Test
	void providedRuntimeConfigurationIsNotAddedWithWarProject() {
		GradleBuild build = new GradleBuild();
		build.plugins().add("war");
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("servlet", "jakarta.servlet", "jakarta.servlet-api", DependencyScope.PROVIDED_RUNTIME);
		customize(build);
		assertThat(build.configurations().customizations()).isEmpty();
	}

	@Test
	void providedRuntimeConfigurationIsNotAddedWithNonMatchingDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("another", "com.example", "another", DependencyScope.RUNTIME);
		customize(build);
		assertThat(build.configurations().customizations()).isEmpty();
	}

	private void customize(GradleBuild build) {
		new GradleConfigurationBuildCustomizer().customize(build);
	}

}
