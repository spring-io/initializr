/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.scm.git;

import org.springframework.core.Ordered;

/**
 * Callback for customizing a project's {@link GitAttributes}. Invoked with an
 * {@link Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 *
 * @author Moritz Halbritter
 */
@FunctionalInterface
public interface GitAttributesCustomizer extends Ordered {

	/**
	 * Customize the given git attributes.
	 * @param gitAttributes the git attributes to customize
	 */
	void customize(GitAttributes gitAttributes);

	@Override
	default int getOrder() {
		return 0;
	}

}
