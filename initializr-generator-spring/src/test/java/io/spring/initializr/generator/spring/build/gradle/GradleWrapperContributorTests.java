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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.assertj.core.internal.Failures;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleWrapperContributor}.
 *
 * @author Stephane Nicoll
 */
class GradleWrapperContributorTests {

	@TempDir
	private Path directory;

	@ParameterizedTest(name = "Gradle {0}")
	@ValueSource(strings = "8")
	void gradleWrapperSetExecutableFlagOnScripts(String gradleVersion) throws IOException {
		Path projectDir = contribute(gradleVersion);
		assertThat(projectDir.resolve("gradlew")).isRegularFile().isExecutable();
		assertThat(projectDir.resolve("gradlew.bat")).isRegularFile().isExecutable();
		assertThat(projectDir.resolve("gradle/wrapper/gradle-wrapper.jar")).isRegularFile()
			.satisfies(isNotExecutable());
		assertThat(projectDir.resolve("gradle/wrapper/gradle-wrapper.properties")).isRegularFile()
			.satisfies(isNotExecutable());
	}

	private Consumer<Path> isNotExecutable() {
		return (path) -> {
			if (supportsExecutableFlag() && Files.isExecutable(path)) {
				throw Failures.instance().failure(String.format("%nExpecting:%n  <%s>%nto not be executable.", path));
			}
		};
	}

	private static boolean supportsExecutableFlag() {
		return !System.getProperty("os.name").startsWith("Windows");
	}

	Path contribute(String gradleVersion) throws IOException {
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		new GradleWrapperContributor(gradleVersion).contribute(projectDir);
		return projectDir;
	}

}
