/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.packaging.war;

import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.PackagingFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link PackagingFactory Factory} for {@link WarPackaging}.
 *
 * @author Andy Wilkinson
 */
class WarPackagingFactory implements PackagingFactory {

	@Override
	public @Nullable Packaging createPackaging(@Nullable String id) {
		if (WarPackaging.ID.equals(id)) {
			return new WarPackaging();
		}
		return null;
	}

}
