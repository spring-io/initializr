/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

import java.util.ArrayList;
import java.util.List;

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
