/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.TypeDeclaration;

import org.springframework.core.Ordered;

/**
 * Callback for customizing the application's main compilation unit. Invoked with an
 * {@link Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 *
 * @param <T> language-specific type declaration
 * @param <C> language-specific compilation unit
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface MainCompilationUnitCustomizer<T extends TypeDeclaration, C extends CompilationUnit<T>>
		extends Ordered {

	/**
	 * Customize the given compilation unit.
	 * @param compilationUnit the compilation unit to customize
	 */
	void customize(C compilationUnit);

	@Override
	default int getOrder() {
		return 0;
	}

}
