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
 * The default {@link ProjectAssetGenerator}. Generates a directory structure with all
 * available {@link ProjectContributor project contributors}.
 *
 * @author Stephane Nicoll
 */
public class DefaultProjectAssetGenerator implements ProjectAssetGenerator<Path> {

	@Override
	public Path generate(ProjectGenerationContext context) throws IOException {
		ResolvedProjectDescription resolvedProjectDescription = context
				.getBean(ResolvedProjectDescription.class);
		Path projectRoot = context.getBean(ProjectDirectoryFactory.class)
				.createProjectDirectory(resolvedProjectDescription);
		Path projectDirectory = initializerProjectDirectory(projectRoot,
				resolvedProjectDescription);
		List<ProjectContributor> contributors = context
				.getBeanProvider(ProjectContributor.class).orderedStream()
				.collect(Collectors.toList());
		for (ProjectContributor contributor : contributors) {
			contributor.contribute(projectDirectory);
		}
		return projectRoot;
	}

	private Path initializerProjectDirectory(Path rootDir,
			ResolvedProjectDescription description) throws IOException {
		if (description.getBaseDirectory() != null) {
			Path dir = rootDir.resolve(description.getBaseDirectory());
			Files.createDirectories(dir);
			return dir;
		}
		else {
			return rootDir;
		}
	}

}
