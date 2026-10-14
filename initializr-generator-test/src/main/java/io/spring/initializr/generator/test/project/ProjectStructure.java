/*
 * Copyright 2012 - present the original author or authors.
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
					"No directory '%s' found in '%s'".formatted(name, this.projectDirectory));
		}
		return new ProjectStructure(projectDir);
	}

}
