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

package io.spring.initializr.generator.test.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.spring.initializr.generator.language.java.JavaLanguage;
import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link ModuleAssert}.
 *
 * @author Stephane Nicoll
 */
class ModuleAssertTests {

	@Test
	void containDirectories(@TempDir Path dir) throws IOException {
		createDirectories(dir, "test", "test/another", "another");
		assertThat(forDirectory(dir)).containsDirectories("test", "test/another");
	}

	@Test
	void containDirectoriesWithMissingDirectory(@TempDir Path dir) throws IOException {
		createDirectories(dir, "test", "test/another", "another");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).containsDirectories("test", "wrong"));
	}

	@Test
	void doesNotContainDirectories(@TempDir Path dir) throws IOException {
		createDirectories(dir, "test", "test/another");
		assertThat(forDirectory(dir)).doesNotContainDirectories("another", "test/something");
	}

	@Test
	void doesNotContainDirectoriesWithExistingDirectory(@TempDir Path dir) throws IOException {
		createDirectories(dir, "test", "test/another");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).doesNotContainDirectories("another", "test/another"));
	}

	@Test
	void containFiles(@TempDir Path dir) throws IOException {
		createFiles(dir, "test.xml", "src/Test.java", "another");
		assertThat(forDirectory(dir)).containsFiles("src/Test.java", "another");
	}

	@Test
	void containFilesWithMissingFile(@TempDir Path dir) throws IOException {
		createFiles(dir, "test.xml", "src/Test.java", "another");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).containsFiles("test.xml", "wrong"));
	}

	@Test
	void doesNotContainFiles(@TempDir Path dir) throws IOException {
		createFiles(dir, "test.xml", "src/Test.java");
		assertThat(forDirectory(dir)).doesNotContainFiles("another", "test/Another.java");
	}

	@Test
	void doesNotContainFilesWithExistingFile(@TempDir Path dir) throws IOException {
		createFiles(dir, "test.xml", "src/Test.java");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).doesNotContainFiles("another", "src/Test.java"));
	}

	@Test
	void filePaths(@TempDir Path dir) throws IOException {
		createFiles(dir, "src/One.java", "src/com/example/Two.java", "pom.xml", ".gitignore");
		createDirectories(dir, "test/unrelated");
		assertThat(forDirectory(dir)).filePaths().containsOnly("src/One.java", "src/com/example/Two.java", "pom.xml",
				".gitignore");
	}

	@Test
	void file(@TempDir Path dir) throws IOException {
		createFiles(dir, "some/file/here.txt");
		assertThat(forDirectory(dir)).file("some/file/here.txt").isEqualTo(dir.resolve("some/file/here.txt"));
	}

	@Test
	void fileWithMissingFile(@TempDir Path dir) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).file("som/file/does-not-exist.txt"));
	}

	@Test
	void textFile(@TempDir Path dir) throws IOException {
		createFileFrom(new ClassPathResource("project/build/maven/sample-pom.xml"), dir.resolve("test.xml"));
		assertThat(forDirectory(dir)).textFile("test.xml").contains("<dependencies>", "<version>");
	}

	@Test
	void textFileWithMissingFile(@TempDir Path dir) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).textFile("som/file/does-not-exist.txt"));
	}

	@Test
	void asJavaProject(@TempDir Path dir) throws IOException {
		createFiles(dir, "src/main/java/com/example/Test.java");
		assertThat(forDirectory(dir)).asJvmModule(new JavaLanguage()).hasMainPackage("com.example")
				.hasMainSource("com.example", "Test");
	}

	@Test
	void hasMavenBuild(@TempDir Path dir) throws IOException {
		createFiles(dir, "pom.xml");
		assertThat(forDirectory(dir)).hasMavenBuild();
	}

	@Test
	void hasMavenBuildWithMissingPomFile(@TempDir Path dir) throws IOException {
		createFiles(dir, "pom.wrong");
		assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forDirectory(dir)).hasMavenBuild());
	}

	@Test
	void hasMavenWrapper(@TempDir Path dir) throws IOException {
		createFiles(dir, "mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties", ".mvn/wrapper/maven-wrapper.jar");
		assertThat(forDirectory(dir)).hasMavenWrapper();
	}

	@Test
	void hasMavenWrapperWithMissingScript(@TempDir Path dir) throws IOException {
		createFiles(dir, ".mvn/wrapper/maven-wrapper.properties", ".mvn/wrapper/maven-wrapper.jar");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).hasMavenWrapper());
	}

	@Test
	void hasMavenWrapperWithMissingDotMvnDir(@TempDir Path dir) throws IOException {
		createFiles(dir, "mvnw", "mvnw.cmd");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).hasMavenWrapper());
	}

	@Test
	void mavenBuild(@TempDir Path dir) throws IOException {
		createFileFrom(new ClassPathResource("project/build/maven/sample-pom.xml"), dir.resolve("pom.xml"));
		assertThat(forDirectory(dir)).mavenBuild().hasGroupId("com.example").hasArtifactId("demo");
	}

	@Test
	void mavenBuildWithMissingPomFile(@TempDir Path dir) {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forDirectory(dir)).mavenBuild());
	}

	@Test
	void hasGroovyDslGradleBuild(@TempDir Path dir) throws IOException {
		createFiles(dir, "build.gradle");
		assertThat(forDirectory(dir)).hasGroovyDslGradleBuild();
	}

	@Test
	void hasGroovyDslGradleBuildWithMissingBuildFile(@TempDir Path dir) throws IOException {
		createFiles(dir, "build.wrong");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).hasGroovyDslGradleBuild());
	}

	@Test
	void hasGradleWrapper(@TempDir Path dir) throws IOException {
		createFiles(dir, "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		assertThat(forDirectory(dir)).hasGradleWrapper();
	}

	@Test
	void hasGradleWrapperWithMissingScript(@TempDir Path dir) throws IOException {
		createFiles(dir, "gradle/wrapper/gradle-wrapper.properties", "gradle/wrapper/gradle-wrapper.jar");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).hasGradleWrapper());
	}

	@Test
	void hasGradleWrapperWithMissingGradleDir(@TempDir Path dir) throws IOException {
		createFiles(dir, "gradlew", "gradlew.bat");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).hasGradleWrapper());
	}

	@Test
	void groovyDslGradleBuild(@TempDir Path dir) throws IOException {
		createFileFrom(new ClassPathResource("project/build/gradle/sample-build.gradle"), dir.resolve("build.gradle"));
		assertThat(forDirectory(dir)).groovyDslGradleBuild().hasVersion("0.0.1-SNAPSHOT").hasSourceCompatibility("1.8");
	}

	@Test
	void groovyDslGradleBuildWithMissingBuildFile(@TempDir Path dir) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forDirectory(dir)).groovyDslGradleBuild());
	}

	private AssertProvider<ModuleAssert> forDirectory(Path projectDirectory) {
		return () -> new ModuleAssert(projectDirectory);
	}

	private void createDirectories(Path dir, String... directories) throws IOException {
		for (String directory : directories) {
			Files.createDirectories(dir.resolve(directory));
		}
	}

	private void createFiles(Path dir, String... files) throws IOException {
		for (String file : files) {
			Path path = dir.resolve(file);
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}
	}

	private void createFileFrom(Resource from, Path target) throws IOException {
		Files.createDirectories(target.getParent());
		FileCopyUtils.copy(from.getInputStream(), Files.newOutputStream(target, StandardOpenOption.CREATE));
	}

}
