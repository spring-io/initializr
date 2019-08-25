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

package io.spring.initializr.web.project;

import java.nio.file.Path;

import io.spring.initializr.generator.project.ProjectDescription;

/**
 * Result of project generation.
 *
 * @author Stephane Nicoll
 */
public class ProjectGenerationResult {

	private final ProjectDescription description;

	private final Path rootDirectory;

	ProjectGenerationResult(ProjectDescription description, Path rootDirectory) {
		this.description = description;
		this.rootDirectory = rootDirectory;
	}

	/**
	 * Return the {@link ProjectDescription} that was used to generate the project.
	 * @return the project description
	 */
	public ProjectDescription getProjectDescription() {
		return this.description;
	}

	/**
	 * Return the root directory.
	 * @return the root directory
	 * @see ProjectDescription#getBaseDirectory()
	 */
	public Path getRootDirectory() {
		return this.rootDirectory;
	}

}
