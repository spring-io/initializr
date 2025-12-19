/*
 * Copyright 2012 - present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.configuration.format;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.core.io.support.SpringFactoriesLoader;

public interface ConfigurationFileFormat {

	/**
	 * Return the id of the configuration file format.
	 * @return the id
	 */
	String id();

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
