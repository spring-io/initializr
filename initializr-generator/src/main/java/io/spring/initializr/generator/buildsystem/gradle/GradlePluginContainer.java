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
 * A container for {@link GradlePlugin}s.
 *
 * @author HaiTao Zhang
 */
public class GradlePluginContainer {

	private final Map<String, GradlePlugin> plugins;

	public GradlePluginContainer() {
		this.plugins = new LinkedHashMap<>();
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link GradlePlugin} is added
	 */
	public boolean isEmpty() {
		return this.plugins.isEmpty();
	}

	/**
	 * Specify if this container has a GradlePlugin with the specified id.
	 * @param id id associated with the {@link GradlePlugin}
	 * @return {@code true} if an object with the specified id is added
	 */
	public boolean has(String id) {
		return this.plugins.containsKey(id);
	}

	/**
	 * Returns a {@link Stream} of added {@link GradlePlugin}s.
	 * @return a stream of {@link GradlePlugin}s
	 */
	public Stream<GradlePlugin> values() {
		return this.plugins.values().stream();
	}

	/**
	 * Add a {@link GradlePlugin} to the Gradle's legacy apply block by specifying the id.
	 * @param id id associated with the {@link GradlePlugin}
	 */
	public void apply(String id) {
		addPlugin(id, (pluginId) -> new GradlePlugin(pluginId, true));
	}

	/**
	 * Add a {@link GradlePlugin} to the Gradle's standard plugins DSL block by specifying
	 * the id.
	 * @param id id associated with the {@link GradlePlugin}
	 */
	public void add(String id) {
		addPlugin(id, (pluginId) -> new StandardGradlePlugin(pluginId));
	}

	/**
	 * Add a {@link GradlePlugin} to the Gradle's standard plugins DSL block by specifying
	 * the id, along with a {@link Consumer} to customize the object.
	 * @param id id associated with the {@link GradlePlugin}
	 * @param plugin consumer to customize the {@link GradlePlugin}
	 */
	public void add(String id, Consumer<StandardGradlePlugin> plugin) {
		GradlePlugin gradlePlugin = addPlugin(id, (pluginId) -> new StandardGradlePlugin(pluginId));
		if (gradlePlugin instanceof StandardGradlePlugin) {
			plugin.accept((StandardGradlePlugin) gradlePlugin);
		}
	}

	public boolean remove(String id) {
		return this.plugins.remove(id) != null;
	}

	private GradlePlugin addPlugin(String id, Function<String, GradlePlugin> pluginId) {
		return this.plugins.computeIfAbsent(id, pluginId);
	}

}
