/*
 * Copyright 2012 - present the original author or authors.
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
