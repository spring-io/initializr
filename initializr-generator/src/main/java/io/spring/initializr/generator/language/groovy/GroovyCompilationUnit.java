/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.language.groovy;

import io.spring.initializr.generator.language.CompilationUnit;

/**
 * A Groovy-specific {@link CompilationUnit}.
 *
 * @author Stephane Nicoll
 */
public class GroovyCompilationUnit extends CompilationUnit<GroovyTypeDeclaration> {

	GroovyCompilationUnit(String packageName, String name) {
		super(packageName, name);
	}

	@Override
	protected GroovyTypeDeclaration doCreateTypeDeclaration(String name) {
		return new GroovyTypeDeclaration(name);
	}

}
