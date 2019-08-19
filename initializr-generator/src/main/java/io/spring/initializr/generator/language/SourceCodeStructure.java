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
 * Provide dedicated method for directories that hold code.
 *
 * @author Stephane Nicoll
 */
public class SourceCodeStructure {

	private final Path rootDirectory;

	public SourceCodeStructure(Path rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public Path getRootDirectory() {
		return this.rootDirectory;
	}

	/**
	 * Resource a source file, creating its package structure if necessary.
	 * @param packageName the name of the package
	 * @param file the name of the file (including its extension)
	 * @return the file to use to store a {@code CompilationUnit} with the specified
	 * package name name
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure
	 */
	public Path resolveSourceFile(String packageName, String file) throws IOException {
		return createPackage(packageName).resolve(file);
	}

	/**
	 * Create the specified package if necessary.
	 * @param packageName the name of the package to create
	 * @return the directory where source code to this package should reside
	 * @throws IOException if an error occurred while trying to create the directory
	 * structure
	 */
	public Path createPackage(String packageName) throws IOException {
		if (!Files.exists(this.rootDirectory)) {
			Files.createDirectories(this.rootDirectory);
		}
		Path directory = this.rootDirectory.resolve(packageName.replace('.', '/'));
		Files.createDirectories(directory);
		return directory;
	}

}
