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

package io.spring.initializr.generator.spring.dependency.devtools;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleDependency;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DevToolsGradleBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class DevToolsGradleBuildCustomizerTests {

	private static final Dependency DEVTOOLS_DEPENDENCY = Dependency
			.withCoordinates("org.springframework.boot", "spring-boot-devtools").build();

	@Test
	void gradleWithDevtoolsAndSpringBootPriorTo23RC1CreatesDevelopmentOnlyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new DevToolsGradleBuildCustomizer(Version.parse("2.3.0.M4"), "devtools").customize(build);
		assertThat(build.configurations().names()).containsOnly("developmentOnly");
		assertThat(build.configurations().customizations()).singleElement().satisfies((configuration) -> {
			assertThat(configuration.getName()).isEqualTo("runtimeClasspath");
			assertThat(configuration.getExtendsFrom()).containsOnly("developmentOnly");
		});
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(GradleDependency.class);
		assertThat(((GradleDependency) devtools).getConfiguration()).isEqualTo("developmentOnly");
	}

	@Test
	void gradleWithDevtoolsAndSpringBoot23RC1OrLaterDoesNotCreateDevelopmentOnlyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new DevToolsGradleBuildCustomizer(Version.parse("2.3.0.RC1"), "devtools").customize(build);
		assertThat(build.configurations().names()).isEmpty();
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(GradleDependency.class);
		assertThat(((GradleDependency) devtools).getConfiguration()).isEqualTo("developmentOnly");
	}

	@Test
	void gradleWithoutDevtoolsAndSpringBootPriorTo23RC1DoesNotCreateDevelopmentOnlyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("web", mock(Dependency.class));
		new DevToolsGradleBuildCustomizer(Version.parse("2.3.0.M4"), "devtools").customize(build);
		assertThat(build.configurations().names()).isEmpty();
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

}
