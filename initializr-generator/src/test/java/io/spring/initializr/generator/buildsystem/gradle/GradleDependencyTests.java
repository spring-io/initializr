/*
 * Copyright 2012-2019 the original author or authors.
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
				.version(VersionReference.ofValue("1.0.0")).scope(DependencyScope.RUNTIME)
				.type("zip").build();
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
				.version(VersionReference.ofValue("1.0.0")).scope(DependencyScope.RUNTIME)
				.type("zip").configuration("myConfiguration").build();
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
