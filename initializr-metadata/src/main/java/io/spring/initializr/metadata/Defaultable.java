/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.metadata;

import org.jspecify.annotations.Nullable;

/**
 * Metadata with a default value.
 *
 * @param <T> the value type
 * @author Dave Syer
 */
public interface Defaultable<T> {

	/**
	 * Return the default value.
	 * @return the default value
	 */
	@Nullable T getDefault();

}
