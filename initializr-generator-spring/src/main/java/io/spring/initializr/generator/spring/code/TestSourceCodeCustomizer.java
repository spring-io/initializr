/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;

import org.springframework.core.Ordered;

/**
 * Callback for customizing the application's test source code. Invoked with an
 * {@link Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 *
 * @param <T> language-specific type declaration
 * @param <C> language-specific compilation unit
 * @param <S> language-specific source code
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface TestSourceCodeCustomizer<T extends TypeDeclaration, C extends CompilationUnit<T>, S extends SourceCode<T, C>>
		extends Ordered {

	/**
	 * Customize the given source code.
	 * @param sourceCode the source code to customize
	 */
	void customize(S sourceCode);

	@Override
	default int getOrder() {
		return 0;
	}

}
