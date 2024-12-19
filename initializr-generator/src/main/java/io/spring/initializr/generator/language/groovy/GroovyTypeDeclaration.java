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

package io.spring.initializr.generator.language.groovy;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.language.TypeDeclaration;

/**
 * A {@link TypeDeclaration declaration } of a type written in Groovy.
 *
 * @author Stephane Nicoll
 */
public class GroovyTypeDeclaration extends TypeDeclaration {

	private int modifiers;

	private final List<GroovyFieldDeclaration> fieldDeclarations = new ArrayList<>();

	private final List<GroovyMethodDeclaration> methodDeclarations = new ArrayList<>();

	GroovyTypeDeclaration(String name) {
		super(name);
	}

	/**
	 * Sets the modifiers.
	 * @param modifiers the modifiers
	 */
	public void modifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Returns the modifiers.
	 * @return the modifiers
	 */
	public int getModifiers() {
		return this.modifiers;
	}

	/**
	 * Adds the given field declaration.
	 * @param fieldDeclaration the field declaration
	 */
	public void addFieldDeclaration(GroovyFieldDeclaration fieldDeclaration) {
		this.fieldDeclarations.add(fieldDeclaration);
	}

	/**
	 * Return the field declarations.
	 * @return the field declarations
	 */
	public List<GroovyFieldDeclaration> getFieldDeclarations() {
		return this.fieldDeclarations;
	}

	/**
	 * Adds the given method declaration.
	 * @param methodDeclaration the method declaration
	 */
	public void addMethodDeclaration(GroovyMethodDeclaration methodDeclaration) {
		this.methodDeclarations.add(methodDeclaration);
	}

	/**
	 * Returns the method declarations.
	 * @return the method declarations
	 */
	public List<GroovyMethodDeclaration> getMethodDeclarations() {
		return this.methodDeclarations;
	}

}
