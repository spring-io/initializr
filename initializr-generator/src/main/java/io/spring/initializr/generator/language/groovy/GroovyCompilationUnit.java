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

package io.spring.initializr.generator.language.groovy;

import io.spring.initializr.generator.language.AnnotationHolder;
import io.spring.initializr.generator.language.CompilationUnit;

/**
 * A Groovy-specific {@link CompilationUnit}.
 *
 * @author Stephane Nicoll
 */
public class GroovyCompilationUnit extends CompilationUnit<GroovyTypeDeclaration> {

	GroovyCompilationUnit(String packageName, String name) {
		super(packageName, name);
	}

	@Override
	protected GroovyTypeDeclaration doCreateTypeDeclaration(String name) {
		return new GroovyTypeDeclaration(name);
	}

	/**
	 * Creates a new {@link GroovyTypeDeclaration} with the specified name and
	 * {@link AnnotationHolder}.
	 * @param name the name of the type declaration
	 * @param annotations the annotation holder to use
	 * @return a new GroovyTypeDeclaration instance
	 */
	public GroovyTypeDeclaration createTypeDeclaration(String name, AnnotationHolder annotations) {
		GroovyTypeDeclaration typeDeclaration = new GroovyTypeDeclaration(name, annotations);
		addTypeDeclaration(typeDeclaration);
		return typeDeclaration;
	}

}
