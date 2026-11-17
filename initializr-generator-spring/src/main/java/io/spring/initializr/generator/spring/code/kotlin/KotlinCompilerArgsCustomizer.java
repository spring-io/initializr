/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.List;

import org.springframework.core.Ordered;

/**
 * Callback for customizing the compiler arguments of a Kotlin project. Invoked with an
 * {@link Ordered order} of {@code 0} by default, consider overriding {@link #getOrder()}
 * to customize this behaviour.
 *
 * @author Moritz Halbritter
 * @see KotlinProjectSettings#getCompilerArgs()
 */
@FunctionalInterface
public interface KotlinCompilerArgsCustomizer extends Ordered {

	/**
	 * Customize the given compiler arguments.
	 * @param kotlinVersion the kotlin version of the project
	 * @param compilerArgs the mutable compiler arguments to customize
	 */
	void customize(String kotlinVersion, List<String> compilerArgs);

	@Override
	default int getOrder() {
		return 0;
	}

}
