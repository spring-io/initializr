/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.configuration.format;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * The format of an application's configuration files, for example {@code properties} or
 * {@code yaml}.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
public interface ConfigurationFileFormat {

	/**
	 * Return the id of the configuration file format.
	 * @return the id
	 */
	String id();

	/**
	 * Return the extension of the configuration files of this format, including the
	 * leading dot, for example {@code .yaml}.
	 * @return the file extension
	 */
	String fileExtension();

	/**
	 * Creates the configuration file format for the given id.
	 * @param id the id
	 * @return the configuration file format
	 * @throws IllegalStateException if the configuration file format with the given id
	 * can't be found
	 */
	static ConfigurationFileFormat forId(@Nullable String id) {
		return SpringFactoriesLoader
			.loadFactories(ConfigurationFileFormatFactory.class, ConfigurationFileFormat.class.getClassLoader())
			.stream()
			.map((factory) -> factory.createConfigurationFileFormat(id))
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Unrecognized configuration file format id '" + id + "'"));
	}

}
