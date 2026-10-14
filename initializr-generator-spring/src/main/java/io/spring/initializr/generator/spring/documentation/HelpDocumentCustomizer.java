/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import org.springframework.core.Ordered;

/**
 * Callback for customizing a project's {@link HelpDocument}. Invoked with an
 * {@link Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface HelpDocumentCustomizer extends Ordered {

	/**
	 * Customize the given help document.
	 * @param document the help document to customize
	 */
	void customize(HelpDocument document);

	@Override
	default int getOrder() {
		return 0;
	}

}
