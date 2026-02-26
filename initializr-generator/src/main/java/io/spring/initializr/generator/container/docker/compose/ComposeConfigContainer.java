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

package io.spring.initializr.generator.container.docker.compose;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.util.Assert;

/**
 * A container for {@linkplain ComposeConfig Docker Compose configs}.
 *
 * @author Moritz Halbritter
 */

public class ComposeConfigContainer {

	private final Map<String, ComposeConfig> configs = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no config is registered
	 */
	public boolean isEmpty() {
		return this.configs.isEmpty();
	}

	/**
	 * Specify if this container has a config with the specified {@code name}.
	 * @param name the name of a config
	 * @return {@code true} if a config with the specified {@code name} exists
	 */
	public boolean has(String name) {
		return this.configs.containsKey(name);
	}

	/**
	 * Return the {@link ComposeConfig configs}.
	 * @return the compose configs
	 */
	public Stream<Map.Entry<String, ComposeConfig>> entries() {
		return this.configs.entrySet().stream().sorted(Map.Entry.comparingByKey());
	}

	/**
	 * Adds a new {@code ComposeConfig config}.
	 * @param name the name of the config
	 * @param config the config
	 */
	public void add(String name, ComposeConfig config) {
		Assert.state(!this.configs.containsKey(name), "Duplicate config '%s'".formatted(name));
		this.configs.put(name, config);
	}

	/**
	 * Remove the config with the specified {@code name}.
	 * @param name the name of the config
	 * @return {@code true} if such a config was registered, {@code false} otherwise
	 */
	public boolean remove(String name) {
		return this.configs.remove(name) != null;
	}

}
