/*
 * Copyright 2012 - present the original author or authors.
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
