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

import java.nio.file.Path;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.test.io.TextAssert;
import org.assertj.core.api.PathAssert;

import org.springframework.util.StringUtils;

/**
 * Base class for JVM module assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public abstract class AbstractJvmModuleAssert<SELF extends AbstractJvmModuleAssert<SELF>>
		extends AbstractProjectAssert<SELF> {

	private final SourceStructure mainDirectory;

	private final SourceStructure testDirectory;

	private final String sourceFileExtension;

	protected AbstractJvmModuleAssert(Path projectDirectory, Language language, Class<?> selfType) {
		super(projectDirectory, selfType);
		this.mainDirectory = new SourceStructure(projectDirectory.resolve("src/main/"), language);
		this.testDirectory = new SourceStructure(projectDirectory.resolve("src/test/"), language);
		this.sourceFileExtension = language.sourceFileExtension();
	}

	/**
	 * Assert that the main source defines the specified package.
	 * @param packageName the name of the package
	 * @return {@code this} assertion object
	 */
	public SELF hasMainPackage(String packageName) {
		return hasPackage(this.mainDirectory.getSourcesDirectory(), packageName);
	}

	/**
	 * Assert that the main source defines the specified type.
	 * @param packageName the name of a package
	 * @param name the name of the type
	 * @return {@code this} assertion object
	 */
	public SELF hasMainSource(String packageName, String name) {
		validateAndGetAsset(this.mainDirectory.getSourcesDirectory(), packageName, name);
		return this.myself;
	}

	/**
	 * Assert that the main source defines the specified type and return an
	 * {@link TextAssert assert} for it, to allow chaining of text-specific assertions
	 * from this call.
	 * @param packageName the name of a package
	 * @param name the name of the type
	 * @return a {@link TextAssert} for the specified source
	 */
	public TextAssert mainSource(String packageName, String name) {
		return new TextAssert(validateAndGetAsset(this.mainDirectory.getSourcesDirectory(), packageName, name));
	}

	/**
	 * Assert that the main resources defines the specified resource.
	 * @param resourcePath the path of a resource relative to the {@code resources}
	 * directory
	 * @return {@code this} assertion object
	 */
	public SELF hasMainResource(String resourcePath) {
		return hasResource(this.mainDirectory.getRootDirectory().resolve("resources"), resourcePath);
	}

	/**
	 * Assert that the test source defines the specified package.
	 * @param packageName the name of the package
	 * @return {@code this} assertion object
	 */
	public SELF hasTestPackage(String packageName) {
		return hasPackage(this.testDirectory.getSourcesDirectory(), packageName);
	}

	/**
	 * Assert that the test source defines the specified type.
	 * @param packageName the name of a package
	 * @param name the name of the type
	 * @return {@code this} assertion object
	 */
	public SELF hasTestSource(String packageName, String name) {
		validateAndGetAsset(this.testDirectory.getSourcesDirectory(), packageName, name);
		return this.myself;
	}

	/**
	 * Assert that the test source defines the specified type and return an
	 * {@link TextAssert assert} for it, to allow chaining of text-specific assertions
	 * from this call.
	 * @param packageName the name of a package
	 * @param name the name of the type
	 * @return a {@link TextAssert} for the specified source
	 */
	public TextAssert testSource(String packageName, String name) {
		return new TextAssert(validateAndGetAsset(this.testDirectory.getSourcesDirectory(), packageName, name));
	}

	private SELF hasPackage(Path baseDir, String packageName) {
		Path expected = baseDir.resolve(packageToPath(packageName));
		new PathAssert(expected).exists().isDirectory();
		return this.myself;
	}

	private Path validateAndGetAsset(Path baseDir, String packageName, String name) {
		Path source = resolveSource(baseDir, packageName, name);
		new PathAssert(source)
				.as("Source '%s.%s' not found in package '%s'", name, this.sourceFileExtension, packageName).exists()
				.isRegularFile();
		return source;
	}

	private SELF hasResource(Path baseDir, String relativePath) {
		Path path = baseDir.resolve(relativePath);
		new PathAssert(path).as("Resource '%s' not found", relativePath).exists().isRegularFile();
		return this.myself;
	}

	private Path resolveSource(Path baseDir, String packageName, String name) {
		return baseDir.resolve(createSourceRelativePath(packageName, name));
	}

	private String createSourceRelativePath(String packageName, String name) {
		return packageToPath(packageName) + "/" + name + "." + this.sourceFileExtension;
	}

	private static String packageToPath(String packageName) {
		String packagePath = packageName.replace(".", "/");
		return StringUtils.trimTrailingCharacter(packagePath, '/');
	}

}
