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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A container for {@linkplain GradlePlugin gradle plugins}.
 *
 * @author HaiTao Zhang
 */
public class GradlePluginContainer {

	private final Map<String, GradlePlugin> plugins = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link GradlePlugin} is added
	 */
	public boolean isEmpty() {
		return this.plugins.isEmpty();
	}

	/**
	 * Specify if this container has a plugin with the specified id.
	 * @param id the identifier of a gradle plugin
	 * @return {@code true} if a plugin with the specified {@code id} exists
	 */
	public boolean has(String id) {
		return this.plugins.containsKey(id);
	}

	/**
	 * Returns a {@link Stream} of registered {@link GradlePlugin}s.
	 * @return a stream of {@link GradlePlugin}s
	 */
	public Stream<GradlePlugin> values() {
		return this.plugins.values().stream();
	}

	/**
	 * Add a {@link GradlePlugin} to the standard {@code plugins} block with the specified
	 * id. Does nothing if the plugin has already been added.
	 * @param id the id of the plugin
	 * @see #add(String, Consumer)
	 */
	public void add(String id) {
		addPlugin(id, StandardGradlePlugin::new);
	}

	/**
	 * Add a {@link GradlePlugin} to the standard {@code plugins} block with the specified
	 * id and {@link Consumer} to customize the object. If the plugin has already been
	 * added, the consumer can be used to further tune the existing plugin configuration.
	 * @param id the id of the plugin
	 * @param plugin a {@link Consumer} to customize the {@link GradlePlugin}
	 */
	public void add(String id, Consumer<StandardGradlePlugin> plugin) {
		GradlePlugin gradlePlugin = addPlugin(id, StandardGradlePlugin::new);
		if (gradlePlugin instanceof StandardGradlePlugin) {
			plugin.accept((StandardGradlePlugin) gradlePlugin);
		}
	}

	/**
	 * Apply a {@link GradlePlugin} with the specified id. Does nothing if the plugin has
	 * already been applied.
	 * @param id the id of the plugin
	 */
	public void apply(String id) {
		addPlugin(id, (pluginId) -> new GradlePlugin(pluginId, true));
	}

	private GradlePlugin addPlugin(String id, Function<String, GradlePlugin> pluginId) {
		return this.plugins.computeIfAbsent(id, pluginId);
	}

	/**
	 * Remove the plugin with the specified {@code id}.
	 * @param id the id of the plugin to remove
	 * @return {@code true} if such a plugin was registered, {@code false} otherwise
	 */
	public boolean remove(String id) {
		return this.plugins.remove(id) != null;
	}

}
