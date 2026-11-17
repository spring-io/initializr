/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.scm.git;

import org.springframework.core.Ordered;

/**
 * Callback for customizing a project's {@link GitIgnore}. Invoked with an {@link Ordered
 * order} of {@code 0} by default, considering overriding {@link #getOrder()} to customize
 * this behaviour.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface GitIgnoreCustomizer extends Ordered {

	/**
	 * Customize the given git ignore.
	 * @param gitIgnore the git ignore to customize
	 */
	void customize(GitIgnore gitIgnore);

	@Override
	default int getOrder() {
		return 0;
	}

}
