/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.packaging.jar;

import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.PackagingFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link PackagingFactory Factory} for {@link JarPackaging}.
 *
 * @author Andy Wilkinson
 */
class JarPackagingFactory implements PackagingFactory {

	@Override
	public @Nullable Packaging createPackaging(@Nullable String id) {
		if (JarPackaging.ID.equals(id)) {
			return new JarPackaging();
		}
		return null;
	}

}
