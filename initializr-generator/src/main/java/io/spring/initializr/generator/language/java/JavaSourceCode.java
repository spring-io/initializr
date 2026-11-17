/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
