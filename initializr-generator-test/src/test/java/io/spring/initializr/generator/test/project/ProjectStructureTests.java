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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link ProjectStructure}.
 *
 * @author Stephane Nicoll
 */
class ProjectStructureTests {

	@Test
	void resolveModule(@TempDir Path dir) throws IOException {
		Path moduleDir = dir.resolve("test");
		Files.createDirectories(moduleDir);
		ProjectStructure module = new ProjectStructure(dir).resolveModule("test");
		assertThat(module).isNotNull();
		assertThat(module.getProjectDirectory()).isEqualTo(moduleDir);
	}

	@Test
	void resolveModuleWithFile(@TempDir Path dir) throws IOException {
		Files.createFile(dir.resolve("test"));
		assertThatIllegalArgumentException().isThrownBy(() -> new ProjectStructure(dir).resolveModule("test"));
	}

	@Test
	void resolveModuleWithNonExistingPath(@TempDir Path dir) {
		Path test = dir.resolve("test");
		assertThat(test).doesNotExist();
		assertThatIllegalArgumentException().isThrownBy(() -> new ProjectStructure(dir).resolveModule("test"));
	}

}
