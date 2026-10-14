/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.groovy;

import io.spring.initializr.generator.language.SourceCode;

/**
 * Groovy {@link SourceCode}.
 *
 * @author Stephane Nicoll
 */
public class GroovySourceCode extends SourceCode<GroovyTypeDeclaration, GroovyCompilationUnit> {

	/**
	 * Creates a new instance.
	 */
	public GroovySourceCode() {
		super(GroovyCompilationUnit::new);
	}

}
