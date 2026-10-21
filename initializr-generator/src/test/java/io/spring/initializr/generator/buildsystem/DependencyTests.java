/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
class DependencyTests {

	@Test
	void dependencyWithCoordinatesOnly() {
		Dependency dependency = Dependency.withCoordinates("com.example", "acme").build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isNull();
		assertThat(dependency.getVersion()).isNull();
		assertThat(dependency.getClassifier()).isNull();
		assertThat(dependency.getType()).isNull();
		assertThat(dependency.getExclusions()).isEmpty();
	}

	@Test
	void dependencyWithScopeAndVersionValue() {
		Dependency dependency = Dependency.withCoordinates("com.example", "acme")
			.scope(DependencyScope.RUNTIME)
			.version(VersionReference.ofValue("1.0.0"))
			.build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isEqualTo(DependencyScope.RUNTIME);
		assertThat(dependency.getVersion()).isNotNull();
		assertThat(dependency.getVersion().getValue()).isEqualTo("1.0.0");
		assertThat(dependency.getClassifier()).isNull();
		assertThat(dependency.getType()).isNull();
		assertThat(dependency.getExclusions()).isEmpty();
	}

	@Test
	void dependencyWithClassifier() {
		Dependency dependency = Dependency.withCoordinates("com.example", "acme").classifier("test").build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isNull();
		assertThat(dependency.getVersion()).isNull();
		assertThat(dependency.getClassifier()).isEqualTo("test");
		assertThat(dependency.getType()).isNull();
		assertThat(dependency.getExclusions()).isEmpty();
	}

	@Test
	void dependencyWithType() {
		Dependency dependency = Dependency.withCoordinates("com.example", "acme").type("test-zip").build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isNull();
		assertThat(dependency.getVersion()).isNull();
		assertThat(dependency.getClassifier()).isNull();
		assertThat(dependency.getType()).isEqualTo("test-zip");
		assertThat(dependency.getExclusions()).isEmpty();
	}

	@Test
	void dependencyWithExclusions() {
		Dependency dependency = Dependency.withCoordinates("com.example", "acme")
			.exclusions(new Exclusion("com.example", "exclude1"), new Exclusion("com.example", "exclude2"))
			.build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isNull();
		assertThat(dependency.getVersion()).isNull();
		assertThat(dependency.getClassifier()).isNull();
		assertThat(dependency.getType()).isNull();
		assertThat(dependency.getExclusions()).containsExactly(new Exclusion("com.example", "exclude1"),
				new Exclusion("com.example", "exclude2"));
	}

	@Test
	void exclusionWithInvalidGroupId() {
		assertThatIllegalArgumentException().isThrownBy(() -> new Exclusion("", "acme"))
			.withMessage("GroupId must not be null");
		assertThatIllegalArgumentException().isThrownBy(() -> new Exclusion(" ", "acme"))
			.withMessage("GroupId must not be null");
	}

	@Test
	void exclusionWithInvalidArtifactId() {
		assertThatIllegalArgumentException().isThrownBy(() -> new Exclusion("com.example", ""))
			.withMessage("ArtifactId must not be null");
		assertThatIllegalArgumentException().isThrownBy(() -> new Exclusion("com.example", " "))
			.withMessage("ArtifactId must not be null");
	}

}
