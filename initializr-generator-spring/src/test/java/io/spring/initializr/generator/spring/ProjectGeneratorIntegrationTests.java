/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.spring;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ProjectGenerator;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectGeneratorTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.boot.SpringBootVersion;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGenerator} that uses all available
 * {@link ProjectGenerationConfiguration} instances.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class ProjectGeneratorIntegrationTests {

	private static final String JAVA_VERSION = "17";

	private static final String DEPENDENCY_MANAGEMENT_PLUGIN_VERSION = "1.1.7";

	private ProjectGeneratorTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectGeneratorTester().withDirectory(directory)
			.withIndentingWriterFactory()
			.withBean(InitializrMetadata.class,
					() -> InitializrMetadataTestBuilder.withDefaults()
						.setGradleEnv(DEPENDENCY_MANAGEMENT_PLUGIN_VERSION)
						.build());
	}

	@Test
	void customBaseDirectoryIsUsedWhenGeneratingProject() {
		MutableProjectDescription description = initProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		description.setBaseDirectory("test/demo-app");
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths()
			.containsOnly("test/demo-app/.gitignore", "test/demo-app/.gitattributes", "test/demo-app/pom.xml",
					"test/demo-app/mvnw", "test/demo-app/mvnw.cmd",
					"test/demo-app/.mvn/wrapper/maven-wrapper.properties",
					"test/demo-app/src/main/java/com/example/demo/DemoApplication.java",
					"test/demo-app/src/main/resources/application.properties",
					"test/demo-app/src/test/java/com/example/demo/DemoApplicationTests.java", "test/demo-app/HELP.md");
	}

	@Test
	void generatedMavenProjectBuilds(@TempDir Path mavenHome) throws Exception {
		MutableProjectDescription description = initProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		ProjectStructure project = this.projectTester.generate(description);
		Path projectDirectory = project.getProjectDirectory();
		runBuild(mavenHome, projectDirectory, description);
	}

	@Test
	void generatedGradleProjectBuilds(@TempDir Path gradleHome) throws Exception {
		MutableProjectDescription description = initProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		ProjectStructure project = this.projectTester.generate(description);
		Path projectDirectory = project.getProjectDirectory();
		runBuild(gradleHome, projectDirectory, description);
	}

	private void runBuild(Path mavenHome, Path projectDirectory, MutableProjectDescription description)
			throws InterruptedException, IOException {
		ProcessBuilder processBuilder = createProcessBuilder(projectDirectory, description.getBuildSystem(), mavenHome);
		Path output = projectDirectory.resolve("output.log");
		processBuilder.redirectError(output.toFile());
		processBuilder.redirectOutput(output.toFile());
		assertThat(processBuilder.start().waitFor()).describedAs(String.join("\n", Files.readAllLines(output)))
			.isEqualTo(0);
	}

	private ProcessBuilder createProcessBuilder(Path directory, BuildSystem buildSystem, Path home) {
		String javaHome = System.getProperty("java.home");
		if (buildSystem.id().equals(MavenBuildSystem.ID)) {
			String command = (isWindows()) ? "mvnw.cmd" : "mvnw";
			ProcessBuilder processBuilder = new ProcessBuilder(directory.resolve(command).toAbsolutePath().toString(),
					"-Dmaven.repo.local=" + home.resolve("repository").toAbsolutePath(), "--batch-mode",
					"--no-transfer-progress", "package");
			if (javaHome != null) {
				processBuilder.environment().put("JAVA_HOME", javaHome);
			}
			processBuilder.environment().put("MAVEN_USER_HOME", home.toAbsolutePath().toString());
			processBuilder.directory(directory.toFile());
			return processBuilder;
		}
		if (buildSystem.id().equals(GradleBuildSystem.ID)) {
			String command = (isWindows()) ? "gradlew.bat" : "gradlew";
			ProcessBuilder processBuilder = new ProcessBuilder(directory.resolve(command).toAbsolutePath().toString(),
					"--no-daemon", "build");
			if (javaHome != null) {
				processBuilder.environment().put("JAVA_HOME", javaHome);
			}
			processBuilder.environment().put("GRADLE_USER_HOME", home.toAbsolutePath().toString());
			processBuilder.directory(directory.toFile());
			return processBuilder;
		}
		throw new IllegalStateException("Unknown build system '%s'".formatted(buildSystem.id()));
	}

	private boolean isWindows() {
		String osName = System.getProperty("os.name");
		return osName != null && osName.startsWith("Windows");
	}

	private MutableProjectDescription initProjectDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setApplicationName("DemoApplication");
		description.setPlatformVersion(Version.parse(SpringBootVersion.getVersion()));
		description.setLanguage(new JavaLanguage(JAVA_VERSION));
		description.setGroupId("com.example");
		return description;
	}

}
