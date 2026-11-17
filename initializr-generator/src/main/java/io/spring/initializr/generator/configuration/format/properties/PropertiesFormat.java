/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.configuration.format.properties;

import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;

/**
 * Properties {@link ConfigurationFileFormat}.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
public final class PropertiesFormat implements ConfigurationFileFormat {

	/**
	 * Properties {@link ConfigurationFileFormat} identifier.
	 */
	public static final String ID = "properties";

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String fileExtension() {
		return ".properties";
	}

	@Override
	public String toString() {
		return id();
	}

}
