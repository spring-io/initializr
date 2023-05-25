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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} which contributes a 'compose.yaml' file through a
 * {@link DockerComposeFile}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeProjectContributor implements ProjectContributor {

	private final DockerComposeFile composeFile;

	DockerComposeProjectContributor(DockerComposeFile composeFile) {
		this.composeFile = composeFile;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path file = Files.createFile(projectRoot.resolve("compose.yaml"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			this.composeFile.write(writer);
		}
	}

}
