/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.configuration.format.properties;

import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormatFactory;
import org.jspecify.annotations.Nullable;

/**
 * {@link ConfigurationFileFormat Factory} for {@link PropertiesFormat}.
 *
 * @author Sijun Yang
 */
class PropertiesFormatFactory implements ConfigurationFileFormatFactory {

	@Override
	public @Nullable ConfigurationFileFormat createConfigurationFileFormat(@Nullable String id) {
		if (PropertiesFormat.ID.equals(id)) {
			return new PropertiesFormat();
		}
		return null;
	}

}
