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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SimpleBuildCustomizer}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class SimpleBuildCustomizerTests {

	@Test
	void customizeProjectCoordinates() {
		MutableProjectDescription description = initializeDescription();
		description.setGroupId("com.example.acme");
		description.setArtifactId("my-test-project");
		MavenBuild build = customizeBuild(description);
		assertThat(build.getSettings().getGroup()).isEqualTo("com.example.acme");
		assertThat(build.getSettings().getArtifact()).isEqualTo("my-test-project");
	}

	@Test
	void customizeVersion() {
		MutableProjectDescription description = initializeDescription();
		description.setVersion("2.4.0");
		MavenBuild build = customizeBuild(description);
		assertThat(build.getSettings().getVersion()).isEqualTo("2.4.0");
	}

	@Test
	void customizeWithNoDependency() {
		MutableProjectDescription description = initializeDescription();
		MavenBuild build = customizeBuild(description);
		assertThat(build.dependencies().ids()).isEmpty();
		assertThat(build.dependencies().items()).isEmpty();
	}

	@Test
	void customizeDependencies() {
		MutableProjectDescription description = initializeDescription();
		Dependency one = Dependency.withCoordinates("com.example", "one").scope(DependencyScope.COMPILE).build();
		Dependency two = Dependency.withCoordinates("com.example.acme", "two").scope(DependencyScope.COMPILE).build();
		description.addDependency("two", two);
		description.addDependency("one", one);
		MavenBuild build = customizeBuild(description);
		assertThat(build.dependencies().ids()).containsExactly("two", "one");
		assertThat(build.dependencies().items()).containsExactly(two, one);
	}

	private MutableProjectDescription initializeDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.0"));
		description.setLanguage(new JavaLanguage());
		return description;
	}

	private MavenBuild customizeBuild(MutableProjectDescription description) {
		MavenBuild build = new MavenBuild();
		SimpleBuildCustomizer customizer = new SimpleBuildCustomizer(description);
		customizer.customize(build);
		return build;
	}

}
