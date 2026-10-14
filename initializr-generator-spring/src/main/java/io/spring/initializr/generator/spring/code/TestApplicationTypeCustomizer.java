/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.language.TypeDeclaration;

import org.springframework.core.Ordered;

/**
 * Callback for customizing the application's test type. Invoked with an {@link Ordered
 * order} of {@code 0} by default, considering overriding {@link #getOrder()} to customize
 * this behaviour.
 *
 * @param <T> type declaration that this customizer can handle
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface TestApplicationTypeCustomizer<T extends TypeDeclaration> extends Ordered {

	/**
	 * Customize the given type declaration.
	 * @param typeDeclaration the type declaration to customize
	 */
	void customize(T typeDeclaration);

	@Override
	default int getOrder() {
		return 0;
	}

}
