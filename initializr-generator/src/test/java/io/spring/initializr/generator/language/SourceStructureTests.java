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
	void createSourceFile(@TempDir Path dir) throws IOException {
		Path target = dir.resolve("src/main/java/com/example/Test.java");
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.createSourceFile("com.example", "Test");
		assertThat(path).exists().isRegularFile().isEqualByComparingTo(target);
	}

	@Test
	void createSourceFileWithExistingPackage(@TempDir Path dir) throws IOException {
		Path rootDir = dir.resolve("src/main/java/com/example");
		Files.createDirectories(rootDir);
		assertThat(rootDir).exists().isDirectory();
		Path target = rootDir.resolve("Test.java");
		assertThat(target).doesNotExist();
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.createSourceFile("com.example", "Test");
		assertThat(path).exists().isRegularFile().isEqualByComparingTo(target);
	}

	@Test
	void resolveSourceWithPath(@TempDir Path dir) {
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path configFile = sourceStructure.getSourcesDirectory().resolve("com/example/specific.xml");
		assertThat(configFile).isEqualByComparingTo(dir.resolve("src/main/java/com/example/specific.xml"));
	}

	@Test
	void createResourceFile(@TempDir Path dir) throws IOException {
		Path target = dir.resolve("src/main/resources/com/example/test.xml");
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.createResourceFile("com.example", "test.xml");
		assertThat(path).exists().isRegularFile().isEqualByComparingTo(target);
	}

	@Test
	void createResourceFileWithExistingPackage(@TempDir Path dir) throws IOException {
		Path rootDir = dir.resolve("src/main/resources/com/example");
		Files.createDirectories(rootDir);
		assertThat(rootDir).exists().isDirectory();
		Path target = rootDir.resolve("test.properties");
		assertThat(target).doesNotExist();
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path path = sourceStructure.createResourceFile("com.example", "test.properties");
		assertThat(path).exists().isRegularFile().isEqualByComparingTo(target);
	}

	@Test
	void resolveResourceWithPath(@TempDir Path dir) {
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path configFile = sourceStructure.getResourcesDirectory().resolve("config.xml");
		assertThat(configFile).isEqualByComparingTo(dir.resolve("src/main/resources/config.xml"));
	}

	@Test
	void resolveBuildResource(@TempDir Path dir) {
		SourceStructure sourceStructure = new SourceStructure(dir.resolve("src/main"), JAVA_LANGUAGE);
		Path configFile = sourceStructure.getRootDirectory().resolve("assembly/bundle.xml");
		assertThat(configFile).isEqualByComparingTo(dir.resolve("src/main/assembly/bundle.xml"));
	}

}
