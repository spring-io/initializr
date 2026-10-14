/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import org.springframework.core.Ordered;

/**
 * Callback for customizing a {@link ProjectDescription}. Invoked with an {@link Ordered
 * order} of {@code 0} by default, considering overriding {@link #getOrder()} to customize
 * this behaviour.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectDescriptionCustomizer extends Ordered {

	/**
	 * Customize the {@linkplain MutableProjectDescription project description}.
	 * @param description the project description to customize
	 */
	void customize(MutableProjectDescription description);

	@Override
	default int getOrder() {
		return 0;
	}

}
