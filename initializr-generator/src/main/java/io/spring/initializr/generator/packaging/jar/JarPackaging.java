/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.packaging.jar;

import io.spring.initializr.generator.packaging.Packaging;

/**
 * Jar {@link Packaging}.
 *
 * @author Andy Wilkinson
 */
public final class JarPackaging implements Packaging {

	/**
	 * Jar {@link Packaging} identifier.
	 */
	public static final String ID = "jar";

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String toString() {
		return id();
	}

}
