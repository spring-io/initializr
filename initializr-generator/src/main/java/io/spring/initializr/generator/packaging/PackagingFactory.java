/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.packaging;

import org.jspecify.annotations.Nullable;

/**
 * A factory for creating a {@link Packaging}.
 *
 * @author Andy Wilkinson
 */
public interface PackagingFactory {

	/**
	 * Creates and returns a {@link Packaging} for the given id. If the factory does not
	 * recognize the given {@code id}, {@code null} should be returned.
	 * @param id the id of the packaging
	 * @return the packaging or {@code null}
	 */
	@Nullable Packaging createPackaging(@Nullable String id);

}
