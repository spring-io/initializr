/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.configuration.format.yaml;

import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormatFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link ConfigurationFileFormat Factory} for {@link YamlFormat}.
 *
 * @author Sijun Yang
 */
class YamlFormatFactory implements ConfigurationFileFormatFactory {

	@Override
	public @Nullable ConfigurationFileFormat createConfigurationFileFormat(@Nullable String id) {
		if (YamlFormat.ID.equals(id)) {
			return new YamlFormat();
		}
		return null;
	}

}
