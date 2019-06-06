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

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleConfigurationBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class GradleConfigurationBuildCustomizerTests {

	@Test
	void providedRuntimeConfigurationIsAddedWithNonWarProject() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("servlet", "javax.servlet", "servlet-api",
				DependencyScope.PROVIDED_RUNTIME);
		customize(build);
		assertThat(build.getConfigurations()).containsOnly("providedRuntime");
	}

	@Test
	void providedRuntimeConfigurationIsNotAddedWithWarProject() {
		GradleBuild build = new GradleBuild();
		build.addPlugin("war");
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("servlet", "javax.servlet", "servlet-api",
				DependencyScope.PROVIDED_RUNTIME);
		customize(build);
		assertThat(build.getConfigurationCustomizations()).isEmpty();
	}

	@Test
	void providedRuntimeConfigurationIsNotAddedWithNonMatchingDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("another", "com.example", "another",
				DependencyScope.RUNTIME);
		customize(build);
		assertThat(build.getConfigurationCustomizations()).isEmpty();
	}

	private void customize(GradleBuild build) {
		new GradleConfigurationBuildCustomizer().customize(build);
	}

}
