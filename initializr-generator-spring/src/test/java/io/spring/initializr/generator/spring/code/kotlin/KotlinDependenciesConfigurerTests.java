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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinDependenciesConfigurer}.
 *
 * @author Andy Wilkinson
 */
class KotlinDependenciesConfigurerTests {

	@Test
	void configuresDependenciesForGradleBuild() {
		GradleBuild build = new GradleBuild();
		new KotlinDependenciesConfigurer(Version.parse("2.1.0.RELEASE")).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-stdlib",
				"kotlin-reflect");
		Dependency kotlinStdlib = build.dependencies().get("kotlin-stdlib");
		assertThat(kotlinStdlib.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinStdlib.getArtifactId()).isEqualTo("kotlin-stdlib-jdk8");
		assertThat(kotlinStdlib.getVersion()).isNull();
		assertThat(kotlinStdlib.getScope()).isEqualTo(DependencyScope.COMPILE);
		Dependency kotlinReflect = build.dependencies().get("kotlin-reflect");
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
	}

	@Test
	void configuresDependenciesForMavenBuildWithBoot15() {
		MavenBuild build = new MavenBuild();
		new KotlinDependenciesConfigurer(Version.parse("1.5.17.RELEASE"))
				.customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-stdlib",
				"kotlin-reflect");
		Dependency kotlinStdlib = build.dependencies().get("kotlin-stdlib");
		assertThat(kotlinStdlib.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinStdlib.getArtifactId()).isEqualTo("kotlin-stdlib-jdk8");
		assertThat(kotlinStdlib.getVersion()).hasToString("${kotlin.version}");
		assertThat(kotlinStdlib.getScope()).isEqualTo(DependencyScope.COMPILE);
		Dependency kotlinReflect = build.dependencies().get("kotlin-reflect");
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).hasToString("${kotlin.version}");
	}

	@Test
	void configuresDependenciesForMavenBuildWithBoot20() {
		MavenBuild build = new MavenBuild();
		new KotlinDependenciesConfigurer(Version.parse("2.0.6.RELEASE")).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-stdlib",
				"kotlin-reflect");
		Dependency kotlinStdlib = build.dependencies().get("kotlin-stdlib");
		assertThat(kotlinStdlib.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinStdlib.getArtifactId()).isEqualTo("kotlin-stdlib-jdk8");
		assertThat(kotlinStdlib.getVersion()).isNull();
		assertThat(kotlinStdlib.getScope()).isEqualTo(DependencyScope.COMPILE);
		Dependency kotlinReflect = build.dependencies().get("kotlin-reflect");
		assertThat(kotlinReflect.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinReflect.getArtifactId()).isEqualTo("kotlin-reflect");
		assertThat(kotlinReflect.getVersion()).isNull();
	}

}
