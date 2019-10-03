/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.gradle.GradleConfiguration.Builder;

/**
 * A container for custom configuration and {@linkplain GradleConfiguration configuration
 * customizations}.
 *
 * @author Stephane Nicoll
 */
public class GradleConfigurationContainer {

	private final Set<String> configurations = new LinkedHashSet<>();

	private final Map<String, GradleConfiguration.Builder> configurationCustomizations = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no custom configuration is registered or no configuration
	 * is customized
	 */
	public boolean isEmpty() {
		return this.configurations.isEmpty() && this.configurationCustomizations.isEmpty();
	}

	/**
	 * Specify if this container has a configuration with the specified {@code name}.
	 * @param name the name of a configuration
	 * @return {@code true} if a configuration with the specified {@code name} exists
	 */
	public boolean has(String name) {
		return this.configurations.contains(name) || this.configurationCustomizations.containsKey(name);
	}

	/**
	 * Return the configuration names that should be registered.
	 * @return the configuration names
	 */
	public Stream<String> names() {
		return this.configurations.stream();
	}

	/**
	 * Return the configuration that should be customized.
	 * @return the configuration customizations
	 */
	public Stream<GradleConfiguration> customizations() {
		return this.configurationCustomizations.values().stream().map(Builder::build);
	}

	/**
	 * Register a {@code configuration} with the specified name.
	 * @param name the name of a configuration
	 */
	public void add(String name) {
		this.configurations.add(name);
	}

	/**
	 * Customize an existing {@code configuration} with the specified {@code name}. If the
	 * configuration has already been customized, the consumer can be used to further tune
	 * the existing configuration customization.
	 * @param name the name of the configuration to customize
	 * @param configuration a {@link Consumer} to customize the
	 * {@link GradleConfiguration}
	 */
	public void customize(String name, Consumer<Builder> configuration) {
		Builder builder = this.configurationCustomizations.computeIfAbsent(name, Builder::new);
		configuration.accept(builder);
	}

	/**
	 * Remove the configuration with the specified {@code name}.
	 * @param name the name of a configuration to register or customization
	 * @return {@code true} if such a configuration was registered, {@code false}
	 * otherwise
	 */
	public boolean remove(String name) {
		if (this.configurations.remove(name)) {
			return true;
		}
		return this.configurationCustomizations.remove(name) != null;
	}

}
