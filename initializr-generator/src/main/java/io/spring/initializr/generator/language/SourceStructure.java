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

package io.spring.initializr.generator.language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provide dedicated methods for a structure that holds sources.
 *
 * @author Stephane Nicoll
 */
public class SourceStructure {

	private final Path rootDirectory;

	private final String sourceFileExtension;

	private final Path sourcesDirectory;

	private final Path resourcesDirectory;

	public SourceStructure(Path rootDirectory, Language language) {
		this.rootDirectory = rootDirectory;
		this.sourceFileExtension = language.sourceFileExtension();
		this.sourcesDirectory = rootDirectory.resolve(language.id());
		this.resourcesDirectory = rootDirectory.resolve("resources");
	}

	/**
	 * Return the root {@link Path directory} of this structure. Can be used to access
	 * additional resources.
	 * @return the root directory
	 */
	public Path getRootDirectory() {
		return this.rootDirectory;
	}

	/**
	 * Return the sources {@link Path directory} of this structure.
	 * @return the sources directory
	 */
	public Path getSourcesDirectory() {
		return this.sourcesDirectory;
	}

	/**
	 * Return the resources {@link Path directory} of this structure.
	 * @return the resources directory
	 */
	public Path getResourcesDirectory() {
		return this.resourcesDirectory;
	}

	/**
	 * Resolve a source file.
	 * @param packageName the name of the package
	 * @param fileName the name of the file (without its extension)
	 * @return the {@link Path file} to use to store a {@code CompilationUnit} with the
	 * specified package and name
	 * @see #getSourcesDirectory()
	 */
	public Path resolveSourceFile(String packageName, String fileName) {
		String file = fileName + "." + this.sourceFileExtension;
		return resolvePackage(this.sourcesDirectory, packageName).resolve(file);
	}

	/**
	 * Create a source file, creating its package structure if necessary.
	 * @param packageName the name of the package
	 * @param fileName the name of the file (without its extension)
	 * @return the {@link Path file} to use to store a {@code CompilationUnit} with the
	 * specified package and name
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure or the file itself
	 * @see #getSourcesDirectory()
	 */
	public Path createSourceFile(String packageName, String fileName) throws IOException {
		Path sourceFile = resolveSourceFile(packageName, fileName);
		createFile(sourceFile);
		return sourceFile;
	}

	/**
	 * Resolve a resource file defined in the specified package.
	 * @param packageName the name of the package
	 * @param file the name of the file (including its extension)
	 * @return the {@link Path file} to use to store a resource with the specified package
	 * @see #getResourcesDirectory()
	 */
	public Path resolveResourceFile(String packageName, String file) {
		return resolvePackage(this.resourcesDirectory, packageName).resolve(file);
	}

	/**
	 * Create a resource file, creating its package structure if necessary.
	 * @param packageName the name of the package
	 * @param file the name of the file (including its extension)
	 * @return the {@link Path file} to use to store a resource with the specified package
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure or the file itself
	 * @see #getResourcesDirectory()
	 */
	public Path createResourceFile(String packageName, String file) throws IOException {
		Path resourceFile = resolveResourceFile(packageName, file);
		createFile(resourceFile);
		return resourceFile;
	}

	private void createFile(Path file) throws IOException {
		Files.createDirectories(file.getParent());
		Files.createFile(file);
	}

	private static Path resolvePackage(Path directory, String packageName) {
		return directory.resolve(packageName.replace('.', '/'));
	}

}
