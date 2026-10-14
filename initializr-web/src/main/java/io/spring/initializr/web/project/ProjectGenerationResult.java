/*
 * Copyright 2012 - present the original author or authors.
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
