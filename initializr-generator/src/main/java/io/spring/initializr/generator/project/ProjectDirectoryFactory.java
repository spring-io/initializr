/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A factory of project directory.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectDirectoryFactory {

	/**
	 * Create a dedicated project directory for the specified {@link ProjectDescription}.
	 * @param description the description of a project to generate
	 * @return a dedicated existing directory
	 * @throws IOException if creating the directory failed
	 */
	Path createProjectDirectory(ProjectDescription description) throws IOException;

}
