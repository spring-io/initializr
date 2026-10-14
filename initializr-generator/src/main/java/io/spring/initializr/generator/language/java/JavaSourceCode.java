/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.language.SourceCode;

/**
 * Java {@link SourceCode}.
 *
 * @author Andy Wilkinson
 */
public class JavaSourceCode extends SourceCode<JavaTypeDeclaration, JavaCompilationUnit> {

	/**
	 * Creates a new instance.
	 */
	public JavaSourceCode() {
		super(JavaCompilationUnit::new);
	}

}
