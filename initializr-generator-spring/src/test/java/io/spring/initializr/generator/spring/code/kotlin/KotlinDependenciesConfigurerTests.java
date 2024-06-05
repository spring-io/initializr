/*
 * Copyright 2012-2023 the original author or authors.
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
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
		Dependency kotlinTest = build.dependencies().get("kotlin-test-junit5");
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
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
		Dependency kotlinTest = build.dependencies().get("kotlin-test-junit5");
		assertThat(kotlinTest.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinTest.getArtifactId()).isEqualTo("kotlin-test-junit5");
		assertThat(kotlinTest.getScope()).isEqualTo(DependencyScope.TEST_COMPILE);
		assertThat(kotlinTest.getVersion()).isNull();
	}

}
