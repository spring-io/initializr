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
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.ConfigurationCustomization;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleAnnotationProcessorScopeBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class GradleAnnotationProcessorScopeBuildCustomizerTests {

	@Test
	void compileOnlyConfigurationIsAddedWithAnnotationProcessorDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("ap", "com.example", "model-generator",
				DependencyScope.ANNOTATION_PROCESSOR);
		customize(build);
		assertThat(build.getConfigurationCustomizations())
				.containsOnlyKeys("compileOnly");
		ConfigurationCustomization compileOnly = build.getConfigurationCustomizations()
				.get("compileOnly");
		assertThat(compileOnly.getExtendsFrom()).containsOnly("annotationProcessor");
	}

	@Test
	void compileOnlyConfigurationIsNotAddedWithNonMatchingDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("lib", "com.example", "lib", DependencyScope.COMPILE);
		build.dependencies().add("another", "com.example", "another",
				DependencyScope.RUNTIME);
		customize(build);
		assertThat(build.getConfigurationCustomizations()).isEmpty();
	}

	private void customize(GradleBuild build) {
		new GradleAnnotationProcessorScopeBuildCustomizer().customize(build);
	}

}
