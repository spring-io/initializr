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

	private final Language language;

	private final Path sourcesDirectory;

	public SourceStructure(Path rootDirectory, Language language) {
		this.rootDirectory = rootDirectory;
		this.language = language;
		this.sourcesDirectory = rootDirectory.resolve(language.id());
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
	 * @return the source code directory
	 */
	public Path getSourcesDirectory() {
		return this.sourcesDirectory;
	}

	/**
	 * Resolve a source file, creating its package structure if necessary. Does not create
	 * the file itself.
	 * @param packageName the name of the package
	 * @param fileName the name of the file (without its extension)
	 * @return the {@link Path file} to use to store a {@code CompilationUnit} with the
	 * specified package and name
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure
	 */
	public Path resolveSourceFile(String packageName, String fileName) throws IOException {
		String file = fileName + "." + this.language.sourceFileExtension();
		return createPackage(this.sourcesDirectory, packageName).resolve(file);
	}

	/**
	 * Create the specified package if necessary.
	 * @param srcDirectory the source directory for the package
	 * @param packageName the name of the package to create
	 * @return the directory where source for this package should reside
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure
	 */
	protected Path createPackage(Path srcDirectory, String packageName) throws IOException {
		if (!Files.exists(srcDirectory)) {
			Files.createDirectories(srcDirectory);
		}
		Path directory = srcDirectory.resolve(packageName.replace('.', '/'));
		Files.createDirectories(directory);
		return directory;
	}

}
