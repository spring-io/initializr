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

import io.spring.initializr.generator.test.io.TextAssert;
import org.assertj.core.api.AbstractPathAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.PathAssert;

/**
 * Base class for project assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public abstract class AbstractProjectAssert<SELF extends AbstractProjectAssert<SELF>> extends AbstractPathAssert<SELF> {

	private ListAssert<String> filesAssert;

	protected AbstractProjectAssert(Path projectDirectory, Class<?> selfType) {
		super(projectDirectory, selfType);
	}

	/**
	 * Assert the project has the specified directories.
	 * @param directoryPaths the directories relative to the project directory.
	 * @return {@code this} assertion object
	 */
	public SELF containsDirectories(String... directoryPaths) {
		for (String directory : directoryPaths) {
			new PathAssert(this.actual.resolve(directory)).exists().isDirectory();
		}
		return this.myself;
	}

	/**
	 * Assert the project does not have the specified directories.
	 * @param directoryPaths the directory paths relative to the project directory.
	 * @return {@code this} assertion object
	 */
	public SELF doesNotContainDirectories(String... directoryPaths) {
		for (String directory : directoryPaths) {
			new PathAssert(this.actual.resolve(directory)).doesNotExist();
		}
		return this.myself;
	}

	/**
	 * Assert the project has the specified files.
	 * @param filePaths the file paths relative to the project directory.
	 * @return {@code this} assertion object
	 */
	public SELF containsFiles(String... filePaths) {
		filePaths().contains(filePaths);
		return this.myself;
	}

	/**
	 * Assert the project does not have the specified files.
	 * @param filePaths the file paths relative to the project directory.
	 * @return {@code this} assertion object
	 */
	public SELF doesNotContainFiles(String... filePaths) {
		filePaths().doesNotContain(filePaths);
		return this.myself;
	}

	/**
	 * Return an {@link ListAssert assert} for the local file paths of this project, to
	 * allow chaining of list-specific assertions from this call.
	 * @return a {@link ListAssert} with the files of this project
	 */
	public ListAssert<String> filePaths() {
		if (this.filesAssert == null) {
			this.filesAssert = new ListAssert<>(getRelativePathsOfProjectFiles());
		}
		return this.filesAssert;
	}

	/**
	 * Assert that the project defines the specified file and return a {@link PathAssert}
	 * for it, to allow chaining of Path-specific assertions from this call.
	 * @param path the path of the file
	 * @return a {@link PathAssert} for the specified file
	 */
	public PathAssert file(String path) {
		Path file = this.actual.resolve(path);
		return new PathAssert(file).exists().isRegularFile();
	}

	/**
	 * Assert that the project defines the specified file and return a {@link TextAssert}
	 * for it, to allow chaining of text file-specific assertions from this call.
	 * @param path the path of a text file
	 * @return a {@link TextAssert} for the specified text file
	 */
	public TextAssert textFile(String path) {
		Path file = this.actual.resolve(path);
		new PathAssert(file).exists().isRegularFile();
		return new TextAssert(file);
	}

	/**
	 * Return the relative paths of all files. For consistency, always use {@code /} as
	 * path separator.
	 * @return the relative path of all files
	 */
	private List<String> getRelativePathsOfProjectFiles() {
		List<String> relativePaths = new ArrayList<>();
		try {
			Files.walkFileTree(this.actual, new SimpleFileVisitor<Path>() {
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
		String relativePath = this.actual.relativize(file).toString();
		if (FileSystems.getDefault().getSeparator().equals("\\")) {
			return relativePath.replace('\\', '/');
		}
		return relativePath;
	}

}
