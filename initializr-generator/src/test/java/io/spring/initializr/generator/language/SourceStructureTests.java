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

package io.spring.initializr.generator.language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.language.java.JavaLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SourceStructure}.
 *
 * @author Stephane Nicoll
 */
class SourceStructureTests {

	public static final JavaLanguage JAVA_LANGUAGE = new JavaLanguage();

	@Test
	void createPackage(@TempDir Path dir) throws IOException {
		Path target = new SourceStructure(dir, JAVA_LANGUAGE).createPackage(dir, "com.example.test");
		assertThat(target).exists().isDirectory().isEqualByComparingTo(dir.resolve("com/example/test"));
	}

	@Test
	void createExistingPackageReturnsExistingDirectory(@TempDir Path dir) throws IOException {
		Path target = dir.resolve("com/example");
		Files.createDirectories(target);
		assertThat(target).exists().isDirectory();
		Path path = new SourceStructure(dir, JAVA_LANGUAGE).createPackage(dir, "com.example");
		assertThat(path).isEqualByComparingTo(target);
	}

	@Test
	void resolveSourceFile(@TempDir Path dir) throws IOException {
		Path rootDir = dir.resolve("src/main/java/com/example");
		assertThat(rootDir).doesNotExist();
		Path target = rootDir.resolve("Test.java");
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.resolveSourceFile("com.example", "Test");
		assertThat(path).doesNotExist().isEqualByComparingTo(target);
		assertThat(rootDir).exists().isDirectory();
	}

	@Test
	void resolveSourceFileWithExistingPackage(@TempDir Path dir) throws IOException {
		Path rootDir = dir.resolve("src/main/java/com/example");
		Files.createDirectories(rootDir);
		assertThat(rootDir).exists().isDirectory();
		Path target = rootDir.resolve("Test.java");
		assertThat(target).doesNotExist();
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.resolveSourceFile("com.example", "Test");
		assertThat(path).doesNotExist().isEqualByComparingTo(target);
	}

}
