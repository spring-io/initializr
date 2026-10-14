/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.jspecify.annotations.Nullable;

/**
 * An item that can describe itself.
 *
 * @author Dave Syer
 */
public interface Describable {

	/**
	 * Returns the item description.
	 * @return the description
	 */
	@Nullable String getDescription();

}
