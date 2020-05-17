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

package io.spring.initializr.generator.test.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.spring.initializr.generator.language.java.JavaLanguage;
import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link JvmModuleAssert}.
 *
 * @author Stephane Nicoll
 */
class JvmModuleAssertTests {

	private static final JavaLanguage JAVA_LANGUAGE = new JavaLanguage();

	@Test
	void hasMainPackage(@TempDir Path root) throws IOException {
		Files.createDirectories(root.resolve("src/main/java/com/example"));
		assertThat(forJavaProject(root)).hasMainPackage("com.example");
	}

	@Test
	void hasMainSource(@TempDir Path root) throws IOException {
		createFile(root, "src/main/java/com/example/Test.java");
		assertThat(forJavaProject(root)).hasMainSource("com.example", "Test");
	}

	@Test
	void hasMainSourceWithNonMatchingExtension(@TempDir Path root) throws IOException {
		createFile(root, "src/main/java/com/example/Test.other");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).hasMainSource("com.example", "Test"))
				.withMessageContaining(
						String.format("Source '%s' not found in package '%s'", "Test.java", "com.example"));
	}

	@Test
	void hasMainSourceWithNonMatchingSourceDir(@TempDir Path root) throws IOException {
		createFile(root, "src/main/groovy/com/example/Test.java");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).hasMainSource("com.example", "Test"))
				.withMessageContaining(
						String.format("Source '%s' not found in package '%s'", "Test.java", "com.example"));
	}

	@Test
	void mainSource(@TempDir Path root) throws IOException {
		createFileFrom("import com.example.Test;", root.resolve("src/main/java/com/acme/Test.java"));
		assertThat(forJavaProject(root)).mainSource("com.acme", "Test").containsOnlyOnce("import");
	}

	@Test
	void mainSourceWithMissingSource(@TempDir Path root) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).mainSource("com.acme", "Test"));
	}

	@Test
	void hasMainResource(@TempDir Path root) throws IOException {
		createFile(root, "src/main/resources/project/sample.xml");
		assertThat(forJavaProject(root)).hasMainResource("project/sample.xml");
	}

	@Test
	void hasMainResourceWithMissingResource(@TempDir Path root) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).hasMainResource("project/sample.xml"));
	}

	@Test
	void hasTestPackage(@TempDir Path root) throws IOException {
		Files.createDirectories(root.resolve("src/test/java/com/example"));
		assertThat(forJavaProject(root)).hasTestPackage("com.example");
	}

	@Test
	void hasTestSource(@TempDir Path root) throws IOException {
		createFile(root, "src/test/java/com/example/Test.java");
		assertThat(forJavaProject(root)).hasTestSource("com.example", "Test");
	}

	@Test
	void hasTestSourceWithNonMatchingExtension(@TempDir Path root) throws IOException {
		createFile(root, "src/test/java/com/example/Test.other");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).hasTestSource("com.example", "Test"))
				.withMessageContaining(
						String.format("Source '%s' not found in package '%s'", "Test.java", "com.example"));
	}

	@Test
	void hasTestSourceWithNonMatchingSourceDir(@TempDir Path root) throws IOException {
		createFile(root, "src/test/groovy/com/example/Test.java");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).hasTestSource("com.example", "Test"))
				.withMessageContaining(
						String.format("Source '%s' not found in package '%s'", "Test.java", "com.example"));
	}

	@Test
	void testSource(@TempDir Path root) throws IOException {
		createFileFrom("@Test", root.resolve("src/test/java/com/acme/DemoTests.java"));
		assertThat(forJavaProject(root)).testSource("com.acme", "DemoTests").containsOnlyOnce("@Test");
	}

	@Test
	void testSourceWithMissingSource(@TempDir Path root) {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forJavaProject(root)).testSource("com.acme", "DemoTests"));
	}

	private AssertProvider<AbstractJvmModuleAssert<?>> forJavaProject(Path root) {
		return () -> new JvmModuleAssert(root, JAVA_LANGUAGE);
	}

	private void createFile(Path root, String path) throws IOException {
		Path target = root.resolve(path);
		Files.createDirectories(target.getParent());
		Files.createFile(target);
	}

	private void createFileFrom(String content, Path target) throws IOException {
		Files.createDirectories(target.getParent());
		FileCopyUtils.copy(content, Files.newBufferedWriter(target, StandardOpenOption.CREATE));
	}

}
