/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinDependenciesConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
class KotlinDependenciesConfigurerTests {

	@Test
	void configuresDependenciesForGradleBuild() {
		GradleBuild build = new GradleBuild();
		new KotlinDependenciesConfigurer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-reflect", "kotlin-test-junit5");
		Dependency kotlinReflect = build.dependencies().get("kotlin-reflect");
		assertThat(kotlinReflect).isNotNull();
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
		Dependency kotlinTest = build.dependencies().get("kotlin-test-junit5");
		assertThat(kotlinTest).isNotNull();
		assertThat(kotlinTest.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinTest.getArtifactId()).isEqualTo("kotlin-test-junit5");
		assertThat(kotlinTest.getScope()).isEqualTo(DependencyScope.TEST_COMPILE);
		assertThat(kotlinTest.getVersion()).isNull();
	}

	@Test
	void configuresDependenciesForMavenBuild() {
		MavenBuild build = new MavenBuild();
		new KotlinDependenciesConfigurer().customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-reflect", "kotlin-test-junit5");
		Dependency kotlinReflect = build.dependencies().get("kotlin-reflect");
		assertThat(kotlinReflect).isNotNull();
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
		Dependency kotlinTest = build.dependencies().get("kotlin-test-junit5");
		assertThat(kotlinTest).isNotNull();
		assertThat(kotlinTest.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinTest.getArtifactId()).isEqualTo("kotlin-test-junit5");
		assertThat(kotlinTest.getScope()).isEqualTo(DependencyScope.TEST_COMPILE);
		assertThat(kotlinTest.getVersion()).isNull();
	}

}
