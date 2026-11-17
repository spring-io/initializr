/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
