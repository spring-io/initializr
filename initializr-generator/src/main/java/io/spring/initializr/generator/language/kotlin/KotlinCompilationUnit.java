/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.language.kotlin;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.language.AnnotationHolder;
import io.spring.initializr.generator.language.CompilationUnit;

/**
 * A Kotlin-specific {@link CompilationUnit}.
 *
 * @author Stephane Nicoll
 */
public class KotlinCompilationUnit extends CompilationUnit<KotlinTypeDeclaration> {

	private final List<KotlinFunctionDeclaration> topLevelFunctions = new ArrayList<>();

	KotlinCompilationUnit(String packageName, String name) {
		super(packageName, name);
	}

	@Override
	protected KotlinTypeDeclaration doCreateTypeDeclaration(String name) {
		return new KotlinTypeDeclaration(name);
	}

	/**
	 * Creates a new {@link KotlinTypeDeclaration} with the specified name and
	 * {@link AnnotationHolder}.
	 * @param name the name of the type declaration
	 * @param annotations the annotation holder to use
	 * @return a new KotlinTypeDeclaration instance
	 */
	public KotlinTypeDeclaration createTypeDeclaration(String name, AnnotationHolder annotations) {
		KotlinTypeDeclaration typeDeclaration = new KotlinTypeDeclaration(name, annotations);
		addTypeDeclaration(typeDeclaration);
		return typeDeclaration;
	}

	/**
	 * Adds the given function as a top level function.
	 * @param function the function to add
	 */
	public void addTopLevelFunction(KotlinFunctionDeclaration function) {
		this.topLevelFunctions.add(function);
	}

	/**
	 * Returns the top level functions.
	 * @return the top level functions
	 */
	public List<KotlinFunctionDeclaration> getTopLevelFunctions() {
		return this.topLevelFunctions;
	}

}
