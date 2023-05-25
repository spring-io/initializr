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

package io.spring.initializr.generator.spring.container.dockercompose;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerComposeProjectContributor}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeProjectContributorTests {

	private DockerComposeProjectContributor contributor;

	private DockerComposeFile dockerComposeFile;

	@BeforeEach
	void setUp() {
		this.dockerComposeFile = new DockerComposeFile();
		this.contributor = new DockerComposeProjectContributor(this.dockerComposeFile);
	}

	@Test
	void doesNothingIfComposeFileIsEmpty(@TempDir Path tempDir) throws IOException {
		this.contributor.contribute(tempDir);
		assertThat(tempDir.resolve("compose.yaml")).doesNotExist();
	}

	@Test
	void writesComposeYamlFile(@TempDir Path tempDir) throws IOException {
		this.dockerComposeFile.addService(DockerComposeServiceFixtures.service());
		this.contributor.contribute(tempDir);
		assertThat(tempDir.resolve("compose.yaml")).content(StandardCharsets.UTF_8).startsWith("services:");
	}

}
