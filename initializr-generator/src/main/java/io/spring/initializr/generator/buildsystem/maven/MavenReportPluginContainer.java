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

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.maven.MavenReportPlugin.Builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A container for {@link MavenPlugin}s.
 *
 * @author HaiTao Zhang
 */
public class MavenReportPluginContainer {

	private final Map<String, Builder> reportPlugins = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenPlugin} is added
	 */
	public boolean isEmpty() {
		return this.reportPlugins.isEmpty();
	}

	/**
	 * Specify if this container has a plugin with the specified {@code groupId} and
	 * {@code artifactId}.
	 * @param groupId the groupId of the plugin
	 * @param artifactId the artifactId of the plugin
	 * @return {@code true} if an item with the specified {@code groupId} and
	 * {@code artifactId} exists
	 */
	public boolean has(String groupId, String artifactId) {
		return this.reportPlugins.containsKey(pluginKey(groupId, artifactId));
	}

	/**
	 * Returns a {@link Stream} of registered {@link MavenPlugin}s.
	 * @return a stream of {@link MavenPlugin}s
	 */
	public Stream<MavenReportPlugin> values() {
		return this.reportPlugins.values().stream().map(Builder::build);
	}

	/**
	 * Add a {@link MavenPlugin} with the specified {@code groupId} and
	 * {@code artifactId}. Does nothing if the plugin has already been added.
	 * @param groupId the groupId of the plugin
	 * @param artifactId the artifactId of the plugin
	 * @see #add(String, String, Consumer)
	 */
	public void add(String groupId, String artifactId) {
		addReportPlugin(groupId, artifactId);
	}

	/**
	 * Add a {@link MavenPlugin} with the specified {@code groupId} and {@code artifactId}
	 * and {@link Consumer} to customize the plugin. If the plugin has already been added,
	 * the consumer can be used to further tune the existing plugin configuration.
	 * @param groupId the groupId of the plugin
	 * @param artifactId the artifactId of the plugin
	 * @param plugin a {@link Consumer} to customize the {@link MavenPlugin}
	 */
	public void add(String groupId, String artifactId, Consumer<Builder> plugin) {
		plugin.accept(addReportPlugin(groupId, artifactId));
	}

	private Builder addReportPlugin(String groupId, String artifactId) {
		return this.reportPlugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(pluginId) -> new Builder(groupId, artifactId));
	}

	/**
	 * Remove the plugin with the specified {@code groupId} and {@code artifactId}.
	 * @param groupId the groupId of the plugin to remove
	 * @param artifactId the artifactId of the plugin to remove
	 * @return {@code true} if such a plugin was registered, {@code false} otherwise
	 */
	public boolean remove(String groupId, String artifactId) {
		return this.reportPlugins.remove(pluginKey(groupId, artifactId)) != null;
	}

	private String pluginKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

}
