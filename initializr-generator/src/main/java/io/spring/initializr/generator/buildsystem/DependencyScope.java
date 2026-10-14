/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

/**
 * The scopes of dependencies supported by project generation.
 *
 * @author Andy Wilkinson
 */
public enum DependencyScope {

	/**
	 * A dependency that is used as an annotation processor when compiling the main
	 * sources of a project.
	 */
	ANNOTATION_PROCESSOR,

	/**
	 * A dependency that is used to compile a project.
	 */
	COMPILE,

	/**
	 * A dependency that is a compile time only dependency and not used at runtime.
	 */
	COMPILE_ONLY,

	/**
	 * A dependency this is used to run a project.
	 */
	RUNTIME,

	/**
	 * A dependency that is provided and is used to run the project.
	 */
	PROVIDED_RUNTIME,

	/**
	 * A dependency that is used to compile a project's tests.
	 */
	TEST_COMPILE,

	/**
	 * A dependency that is used to compile a project's tests and not used at runtime.
	 */
	TEST_COMPILE_ONLY,

	/**
	 * A dependency this is used to run a project's tests.
	 */
	TEST_RUNTIME,

	/**
	 * A dependency that is used as an annotation processor when compiling the test
	 * sources of a project.
	 */
	TEST_ANNOTATION_PROCESSOR

}
