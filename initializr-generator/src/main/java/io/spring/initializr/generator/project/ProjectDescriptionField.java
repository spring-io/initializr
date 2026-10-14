/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

/**
 * A field of a {@link ProjectDescription} whose modification can be explained using
 * {@link ProjectDescriptionChanges}.
 *
 * @author Moritz Halbritter
 * @see ProjectDescriptionChanges
 */
public enum ProjectDescriptionField {

	/**
	 * The JVM version, as exposed by
	 * {@link io.spring.initializr.generator.language.Language#jvmVersion()}.
	 */
	JVM_VERSION

}
