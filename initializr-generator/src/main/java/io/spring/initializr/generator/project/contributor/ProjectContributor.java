/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.core.Ordered;

/**
 * A callback for contributing on a generated project. Invoked with an {@link Ordered
 * order} of {@code 0} by default, considering overriding {@link #getOrder()} to customize
 * this behaviour.
 *
 * @author Andy Wilkinson
 */
@FunctionalInterface
public interface ProjectContributor extends Ordered {

	/**
	 * Contribute additional resources to the project in the specified root directory.
	 * @param projectRoot the root directory of the project
	 * @throws IOException if contributing a resource failed
	 */
	void contribute(Path projectRoot) throws IOException;

	@Override
	default int getOrder() {
		return 0;
	}

}
