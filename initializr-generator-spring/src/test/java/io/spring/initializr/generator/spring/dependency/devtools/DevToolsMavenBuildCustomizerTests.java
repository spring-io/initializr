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
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DevToolsMavenBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class DevToolsMavenBuildCustomizerTests {

	private static final Dependency DEVTOOLS_DEPENDENCY = Dependency
			.withCoordinates("org.springframework.boot", "spring-boot-devtools").build();

	@Test
	void mavenWithDevtoolsSwitchOptionalFlag() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new DevToolsMavenBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(MavenDependency.class);
		assertThat(((MavenDependency) devtools).isOptional()).isTrue();

	}

	@Test
	void mavenWithoutDevtoolsDoesNotChangeDependencies() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("web", mock(Dependency.class));
		new DevToolsMavenBuildCustomizer("devtools").customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

}
