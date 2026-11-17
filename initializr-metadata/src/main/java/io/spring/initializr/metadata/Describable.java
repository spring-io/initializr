/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
