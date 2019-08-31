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

package io.spring.initializr.generator.buildsystem;

import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link BuildSystem}.
 *
 * @author Stephane Nicoll
 */
class BuildSystemTests {

	@Test
	void gradleBuildSystem() {
		BuildSystem gradle = BuildSystem.forId("gradle");
		assertThat(gradle).isInstanceOf(GradleBuildSystem.class);
		assertThat(gradle.id()).isEqualTo("gradle");
		assertThat(gradle.toString()).isEqualTo("gradle");
	}

	@Test
	void mavenBuildSystem() {
		BuildSystem maven = BuildSystem.forId("maven");
		assertThat(maven).isInstanceOf(MavenBuildSystem.class);
		assertThat(maven.id()).isEqualTo("maven");
		assertThat(maven.toString()).isEqualTo("maven");
	}

	@Test
	void defaultMainSource(@TempDir Path directory) {
		SourceStructure mainCodeStructure = BuildSystem.forId("gradle").getMainSource(directory, new JavaLanguage());
		assertThat(mainCodeStructure.getRootDirectory()).isEqualTo(directory.resolve("src/main"));
		assertThat(mainCodeStructure.getSourcesDirectory()).isEqualTo(directory.resolve("src/main/java"));
	}

	@Test
	void defaultTestSource(@TempDir Path directory) {
		SourceStructure testCodeStructure = BuildSystem.forId("gradle").getTestSource(directory, new KotlinLanguage());
		assertThat(testCodeStructure.getRootDirectory()).isEqualTo(directory.resolve("src/test"));
		assertThat(testCodeStructure.getSourcesDirectory()).isEqualTo(directory.resolve("src/test/kotlin"));
	}

	@Test
	void unknownBuildSystem() {
		assertThatIllegalStateException().isThrownBy(() -> BuildSystem.forId("unknown"))
				.withMessageContaining("Unrecognized build system id 'unknown'");
	}

}
