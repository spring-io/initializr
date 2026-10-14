/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.configuration.format;

import org.jspecify.annotations.Nullable;

/**
 * A factory for creating a {@link ConfigurationFileFormat}.
 *
 * @author Sijun Yang
 */
public interface ConfigurationFileFormatFactory {

	/**
	 * Creates and returns a {@link ConfigurationFileFormat} for the given id. If the
	 * factory does not recognise the given {@code id}, {@code null} should be returned.
	 * @param id the id of the configuration file format
	 * @return the configuration file format or {@code null}
	 */
	@Nullable ConfigurationFileFormat createConfigurationFileFormat(@Nullable String id);

}
