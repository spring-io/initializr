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

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleDependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DevelopmentOnlyDependencyGradleBuildCustomizer}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class DevelopmentOnlyDependencyGradleBuildCustomizerTests {

	private static final Dependency WEB_DEPENDENCY = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-starter-web")
		.build();

	private static final Dependency DEVTOOLS_DEPENDENCY = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-devtools")
		.build();

	@Test
	void shouldAddDevelopmentOnlyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new DevelopmentOnlyDependencyGradleBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(GradleDependency.class);
		assertThat(((GradleDependency) devtools).getConfiguration()).isEqualTo("developmentOnly");
	}

	@Test
	void shouldNotFailOnDuplicateDependencies() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("web", WEB_DEPENDENCY);
		new DevelopmentOnlyDependencyGradleBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(GradleDependency.class);
		assertThat(((GradleDependency) devtools).getConfiguration()).isEqualTo("developmentOnly");
	}

	@Test
	void shouldIgnoreOtherDependencies() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("web", WEB_DEPENDENCY);
		new DevelopmentOnlyDependencyGradleBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(GradleDependency.class);
		assertThat(((GradleDependency) devtools).getConfiguration()).isEqualTo("developmentOnly");
		Dependency web = build.dependencies().get("web");
		assertThat(web).isNotInstanceOf(GradleDependency.class);
	}

	@Test
	void shouldNotChangeDependencies() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("web", WEB_DEPENDENCY);
		new DevelopmentOnlyDependencyGradleBuildCustomizer("devtools").customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

}
