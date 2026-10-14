/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

import io.spring.initializr.generator.language.SourceCode;

/**
 * Kotlin {@link SourceCode}.
 *
 * @author Stephane Nicoll
 */
public class KotlinSourceCode extends SourceCode<KotlinTypeDeclaration, KotlinCompilationUnit> {

	/**
	 * Creates a new instance.
	 */
	public KotlinSourceCode() {
		super(KotlinCompilationUnit::new);
	}

}
