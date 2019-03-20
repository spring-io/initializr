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

package io.spring.initializr.generator.spring.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationPropertiesContributor}.
 *
 * @author Stephane Nicoll
 */
class ApplicationPropertiesContributorTests {

	@TempDir
	Path directory;

	@Test
	void applicationConfigurationWithDefaultSettings() throws IOException {
		Path projectDir = Files.createTempDirectory(this.directory, "project-");
		new ApplicationPropertiesContributor().contribute(projectDir);
		List<String> lines = new ProjectStructure(projectDir)
				.readAllLines("src/main/resources/application.properties");
		assertThat(lines).isEmpty();
	}

}
