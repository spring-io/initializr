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

package io.spring.initializr.generator.spring.scm.git;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.project.contributor.SingleResourceProjectContributor;

/**
 * A {@link SingleResourceProjectContributor} that contributes a {@code .gitignore} file
 * to a project.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class GitIgnoreContributor implements ProjectContributor {

	private final GitIgnore gitIgnore;

	public GitIgnoreContributor(GitIgnore gitIgnore) {
		this.gitIgnore = gitIgnore;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.gitIgnore.isEmpty()) {
			return;
		}
		Path file = Files.createFile(projectRoot.resolve(".gitignore"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			this.gitIgnore.write(writer);
		}
	}

}
