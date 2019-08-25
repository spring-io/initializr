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

package io.spring.initializr.generator.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A default {@link ProjectAssetGenerator} implementation that generates a directory
 * structure with all available {@link ProjectContributor project contributors}. Uses a
 * {@link ProjectDirectoryFactory} to determine the root directory to use based on a
 * {@link ProjectDescription}.
 *
 * @author Stephane Nicoll
 */
public class DefaultProjectAssetGenerator implements ProjectAssetGenerator<Path> {

	private final ProjectDirectoryFactory projectDirectoryFactory;

	/**
	 * Create a new instance with the {@link ProjectDirectoryFactory} to use.
	 * @param projectDirectoryFactory the project directory factory to use
	 */
	public DefaultProjectAssetGenerator(ProjectDirectoryFactory projectDirectoryFactory) {
		this.projectDirectoryFactory = projectDirectoryFactory;
	}

	/**
	 * Create a new instance without an explicit {@link ProjectDirectoryFactory}. A bean
	 * of that type is expected to be available in the context.
	 */
	public DefaultProjectAssetGenerator() {
		this(null);
	}

	@Override
	public Path generate(ProjectGenerationContext context) throws IOException {
		ProjectDescription description = context.getBean(ProjectDescription.class);
		Path projectRoot = resolveProjectDirectoryFactory(context).createProjectDirectory(description);
		Path projectDirectory = initializerProjectDirectory(projectRoot, description);
		List<ProjectContributor> contributors = context.getBeanProvider(ProjectContributor.class).orderedStream()
				.collect(Collectors.toList());
		for (ProjectContributor contributor : contributors) {
			contributor.contribute(projectDirectory);
		}
		return projectRoot;
	}

	private ProjectDirectoryFactory resolveProjectDirectoryFactory(ProjectGenerationContext context) {
		return (this.projectDirectoryFactory != null) ? this.projectDirectoryFactory
				: context.getBean(ProjectDirectoryFactory.class);
	}

	private Path initializerProjectDirectory(Path rootDir, ProjectDescription description) throws IOException {
		Path projectDirectory = resolveProjectDirectory(rootDir, description);
		Files.createDirectories(projectDirectory);
		return projectDirectory;
	}

	private Path resolveProjectDirectory(Path rootDir, ProjectDescription description) {
		if (description.getBaseDirectory() != null) {
			return rootDir.resolve(description.getBaseDirectory());
		}
		return rootDir;
	}

}
