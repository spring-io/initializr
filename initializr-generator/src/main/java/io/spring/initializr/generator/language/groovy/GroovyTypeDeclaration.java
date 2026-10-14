/*
 * Copyright 2012 - present the original author or authors.
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
