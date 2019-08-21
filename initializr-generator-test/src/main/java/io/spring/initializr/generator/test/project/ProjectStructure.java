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

import java.nio.file.Files;
import java.nio.file.Path;

import org.assertj.core.api.AssertProvider;

/**
 * Represent a generated project structure and act as an entry point for AssertJ
 * assertions.
 *
 * @author Stephane Nicoll
 */
public final class ProjectStructure implements AssertProvider<ModuleAssert> {

	private final Path projectDirectory;

	/**
	 * Create an instance based on the specified project {@link Path directory}.
	 * @param projectDirectory the project's root directory
	 */
	public ProjectStructure(Path projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	@Override
	public ModuleAssert assertThat() {
		return new ModuleAssert(this.getProjectDirectory());
	}

	/**
	 * Return the project directory.
	 * @return the project directory
	 */
	public Path getProjectDirectory() {
		return this.projectDirectory;
	}

	/**
	 * Resolve a {@link ProjectStructure} based on the specified module name.
	 * @param name the name of a sub-directory of the current project
	 * @return a new {@link ProjectStructure} for the sub-directory
	 */
	public ProjectStructure resolveModule(String name) {
		Path projectDir = this.projectDirectory.resolve(name);
		if (!Files.isDirectory(projectDir)) {
			throw new IllegalArgumentException(
					String.format("No directory '%s' found in '%s'", name, this.projectDirectory));
		}
		return new ProjectStructure(projectDir);
	}

}
