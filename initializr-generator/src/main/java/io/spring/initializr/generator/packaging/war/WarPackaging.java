/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.packaging.war;

import io.spring.initializr.generator.packaging.Packaging;

/**
 * War {@link Packaging}.
 *
 * @author Andy Wilkinson
 */
public final class WarPackaging implements Packaging {

	/**
	 * War {@link Packaging} identifier.
	 */
	public static final String ID = "war";

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String toString() {
		return id();
	}

}
