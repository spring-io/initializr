/*
 * Copyright 2012 - present the original author or authors.
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
		checkInsideRootDirectory(sourceFile);
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
		checkInsideRootDirectory(resourceFile);
		createFile(resourceFile);
		return resourceFile;
	}

	private void checkInsideRootDirectory(Path file) {
		Path normalizedFile = file.normalize();
		if (!normalizedFile.startsWith(this.rootDirectory.normalize())) {
			throw new IllegalArgumentException(
					"File '%s' must be inside the project root directory".formatted(normalizedFile));
		}
	}

	private void createFile(Path file) throws IOException {
		Path parent = file.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		Files.createFile(file);
	}

	private static Path resolvePackage(Path directory, String packageName) {
		return directory.resolve(packageName.replace('.', '/'));
	}

}
