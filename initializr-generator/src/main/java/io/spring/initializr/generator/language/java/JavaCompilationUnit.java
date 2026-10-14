/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.language.CompilationUnit;

/**
 * A Java-specific {@link CompilationUnit}.
 *
 * @author Andy Wilkinson
 */
public class JavaCompilationUnit extends CompilationUnit<JavaTypeDeclaration> {

	JavaCompilationUnit(String packageName, String name) {
		super(packageName, name);
	}

	@Override
	protected JavaTypeDeclaration doCreateTypeDeclaration(String name) {
		return new JavaTypeDeclaration(name);
	}

}
