/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

/**
 * A source set of a generated project, following the standard directory layout of the
 * supported build systems (for example {@code src/main} and {@code src/test}).
 *
 * @author Denis A. Altoé Falqueto
 */
public enum SourceSet {

	/**
	 * The main source set, packaged with the application.
	 */
	MAIN,

	/**
	 * The test source set, only available to tests.
	 */
	TEST

}
