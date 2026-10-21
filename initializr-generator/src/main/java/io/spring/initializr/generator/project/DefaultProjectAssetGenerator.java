/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.jspecify.annotations.Nullable;

import org.springframework.util.FileSystemUtils;

/**
 * A default {@link ProjectAssetGenerator} implementation that generates a directory
 * structure with all available {@link ProjectContributor project contributors}. Uses a
 * {@link ProjectDirectoryFactory} to determine the root directory to use based on a
 * {@link ProjectDescription}. If generation fails, the root directory is deleted.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class DefaultProjectAssetGenerator implements ProjectAssetGenerator<Path> {

	private final @Nullable ProjectDirectoryFactory projectDirectoryFactory;

	/**
	 * Create a new instance with the {@link ProjectDirectoryFactory} to use.
	 * @param projectDirectoryFactory the project directory factory to use
	 */
	public DefaultProjectAssetGenerator(@Nullable ProjectDirectoryFactory projectDirectoryFactory) {
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
		try {
			contribute(context, projectRoot, description);
			return projectRoot;
		}
		catch (IOException | RuntimeException ex) {
			deleteQuietly(projectRoot, ex);
			throw ex;
		}
	}

	private void contribute(ProjectGenerationContext context, Path projectRoot, ProjectDescription description)
			throws IOException {
		Path projectDirectory = initializerProjectDirectory(projectRoot, description);
		List<ProjectContributor> contributors = context.getBeanProvider(ProjectContributor.class)
			.orderedStream()
			.toList();
		for (ProjectContributor contributor : contributors) {
			contributor.contribute(projectDirectory);
		}
	}

	private void deleteQuietly(Path projectRoot, Exception cause) {
		try {
			FileSystemUtils.deleteRecursively(projectRoot);
		}
		catch (IOException ex) {
			cause.addSuppressed(ex);
		}
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
		String baseDirectory = description.getBaseDirectory();
		if (baseDirectory == null) {
			return rootDir;
		}
		Path resolved = rootDir.resolve(baseDirectory).normalize();
		if (!resolved.startsWith(rootDir.normalize())) {
			throw new ProjectGenerationException(
					"Invalid baseDirectory '%s': must be contained within the project root directory"
						.formatted(baseDirectory));
		}
		return resolved;
	}

}
