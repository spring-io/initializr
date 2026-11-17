/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.configuration.format.yaml;

import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;

/**
 * YAML {@link ConfigurationFileFormat}.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
public final class YamlFormat implements ConfigurationFileFormat {

	/**
	 * YAML {@link ConfigurationFileFormat} identifier.
	 */
	public static final String ID = "yaml";

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String fileExtension() {
		return ".yaml";
	}

	@Override
	public String toString() {
		return id();
	}

}
