/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
