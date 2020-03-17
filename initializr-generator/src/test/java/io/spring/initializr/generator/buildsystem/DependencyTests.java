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

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
		Dependency dependency = Dependency.withCoordinates("com.example", "acme").scope(DependencyScope.RUNTIME)
				.version(VersionReference.ofValue("1.0.0")).build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isEqualTo(DependencyScope.RUNTIME);
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
				.exclusions(new Exclusion("com.example", "exclude1"), new Exclusion("com.example", "exclude2")).build();
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("acme");
		assertThat(dependency.getScope()).isNull();
		assertThat(dependency.getVersion()).isNull();
		assertThat(dependency.getClassifier()).isNull();
		assertThat(dependency.getType()).isNull();
		assertThat(dependency.getExclusions()).containsExactly(new Exclusion("com.example", "exclude1"),
				new Exclusion("com.example", "exclude2"));
	}

}
