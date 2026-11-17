/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
