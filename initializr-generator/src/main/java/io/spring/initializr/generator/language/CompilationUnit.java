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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

/**
 * A compilation unit that represents an individual source file.
 *
 * @param <T> the concrete type declaration supported by the compilation unit
 * @author Andy Wilkinson
 */
public abstract class CompilationUnit<T extends TypeDeclaration> {

	private final String packageName;

	private final String name;

	private final List<T> typeDeclarations = new ArrayList<>();

	/**
	 * Create a new instance with the package to use and the name of the type.
	 * @param packageName the package in which the source file should be located
	 * @param name the name of the file
	 */
	public CompilationUnit(String packageName, String name) {
		Assert.hasText(packageName, "'packageName' must not be null");
		Assert.hasText(name, "'name' must not be null");
		this.packageName = packageName;
		this.name = name;
	}

	/**
	 * Return the package name in which the file should reside.
	 * @return the package name
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * Return the name of the source file.
	 * @return the name of the source file
	 */
	public String getName() {
		return this.name;
	}

	public T createTypeDeclaration(String name) {
		T typeDeclaration = doCreateTypeDeclaration(name);
		this.typeDeclarations.add(typeDeclaration);
		return typeDeclaration;
	}

	public List<T> getTypeDeclarations() {
		return Collections.unmodifiableList(this.typeDeclarations);
	}

	protected abstract T doCreateTypeDeclaration(String name);

}
