/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleDependency}.
 *
 * @author Stephane Nicoll
 */
class GradleDependencyTests {

	@Test
	void initializeFromStandardDependency() {
		Dependency original = Dependency.withCoordinates("com.example", "test")
			.version(VersionReference.ofValue("1.0.0"))
			.scope(DependencyScope.RUNTIME)
			.type("zip")
			.build();
		GradleDependency dependency = GradleDependency.from(original).build();
		assertThat(original).isNotSameAs(dependency);
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("test");
		assertThat(dependency.getVersion()).isEqualTo(VersionReference.ofValue("1.0.0"));
		assertThat(dependency.getScope()).isEqualTo(DependencyScope.RUNTIME);
		assertThat(dependency.getType()).isEqualTo("zip");
		assertThat(dependency.getConfiguration()).isNull();
	}

	@Test
	void initializeFromMavenDependency() {
		Dependency original = GradleDependency.withCoordinates("com.example", "test")
			.version(VersionReference.ofValue("1.0.0"))
			.scope(DependencyScope.RUNTIME)
			.type("zip")
			.configuration("myConfiguration")
			.build();
		GradleDependency dependency = GradleDependency.from(original).build();
		assertThat(original).isNotSameAs(dependency);
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("test");
		assertThat(dependency.getVersion()).isEqualTo(VersionReference.ofValue("1.0.0"));
		assertThat(dependency.getScope()).isEqualTo(DependencyScope.RUNTIME);
		assertThat(dependency.getType()).isEqualTo("zip");
		assertThat(dependency.getConfiguration()).isEqualTo("myConfiguration");
	}

}
