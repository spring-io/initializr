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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.test.io.TextTestUtils;

/**
 * Test helper to assert content of a generated project structure.
 *
 * @author Stephane Nicoll
 */
public class ProjectStructure {

	private final Path projectDirectory;

	/**
	 * Create an instance based on the specified project {@link Path directory}.
	 * @param projectDirectory the project's root directory
	 */
	public ProjectStructure(Path projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	/**
	 * Return the project directory.
	 * @return the project directory
	 */
	public Path getProjectDirectory() {
		return this.projectDirectory;
	}

	/**
	 * Resolve a {@link Path} relative to the project directory.
	 * @param other the path string to resolve against the root of the project structure
	 * @return the resulting path
	 * @see Path#resolve(String)
	 */
	public Path resolve(String other) {
		return this.projectDirectory.resolve(other);
	}

	/**
	 * Resolve a {@link Path} relative to the project directory and return all lines.
	 * Check that the resolved {@link Path} is a regular text file that ends with a
	 * newline.
	 * @param other the path string to resolve against the root of the project structure
	 * @return all lines from the resolve file
	 * @see TextTestUtils#readAllLines(Path)
	 */
	public List<String> readAllLines(String other) {
		return TextTestUtils.readAllLines(resolve(other));
	}

	/**
	 * Return the relative paths of all files. For consistency, always use {@code /} as
	 * path separator.
	 * @return the relative path of all files
	 */
	public List<String> getRelativePathsOfProjectFiles() {
		List<String> relativePaths = new ArrayList<>();
		try {
			Files.walkFileTree(this.projectDirectory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					relativePaths.add(createRelativePath(file));
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		return relativePaths;
	}

	private String createRelativePath(Path file) {
		String relativePath = this.projectDirectory.relativize(file).toString();
		if (FileSystems.getDefault().getSeparator().equals("\\")) {
			return relativePath.replace('\\', '/');
		}
		return relativePath;
	}

}
