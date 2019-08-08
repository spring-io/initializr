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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A container for {@link MavenPlugin}s.
 *
 * @author HaiTao Zhang
 */
public class MavenPluginContainer {

	private final Map<String, MavenPlugin> plugins;

	public MavenPluginContainer() {
		this.plugins = new LinkedHashMap<>();
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenPlugin} is added
	 */
	public boolean isEmpty() {
		return this.plugins.isEmpty();
	}

	/**
	 * Specify if this container has a MavenPlugin with the specified groupId and
	 * artifactId.
	 * @param groupId groupId associated with the {@link MavenPlugin}
	 * @param artifactId artifactId associated with the {@link MavenPlugin}
	 * @return {@code true} if an item with the specified {@code groupId} and
	 * {@code artifactId} is added
	 */
	public boolean has(String groupId, String artifactId) {
		return this.plugins.containsKey(pluginKey(groupId, artifactId));
	}

	/**
	 * Returns a {@link Stream} of added {@link MavenPlugin}s.
	 * @return a stream of {@link MavenPlugin}s
	 */
	public Stream<MavenPlugin> values() {
		return this.plugins.values().stream();
	}

	/**
	 * Add a {@link MavenPlugin} by specifying the groupId and artifactId.
	 * @param groupId groupId associated with the {@link MavenPlugin}
	 * @param artifactId artifactId associated with the {@link MavenPlugin}
	 */
	public void add(String groupId, String artifactId) {
		addPlugin(groupId, artifactId);
	}

	/**
	 * Add a {@link MavenPlugin} by specifying the groupId and artifactId, along with a
	 * {@link Consumer} to customize the object.
	 * @param groupId groupId associated with the {@link MavenPlugin}
	 * @param artifactId artifactId associated with the {@link MavenPlugin}
	 * @param plugin {@link Consumer} to customize the object
	 */
	public void add(String groupId, String artifactId, Consumer<MavenPlugin> plugin) {
		MavenPlugin mavenPlugin = addPlugin(groupId, artifactId);
		plugin.accept(mavenPlugin);
	}

	/**
	 * Remove a {@link MavenPlugin} by specifying the groupId and artifactId.
	 * @param groupId groupId associated with the {@link MavenPlugin}
	 * @param artifactId artifactId associated with the {@link MavenPlugin}
	 * @return {@code true} if an object was removed
	 */
	public boolean remove(String groupId, String artifactId) {
		return this.plugins.remove(pluginKey(groupId, artifactId)) != null;
	}

	private String pluginKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

	private MavenPlugin addPlugin(String groupId, String artifactId) {
		return this.plugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(pluginId) -> new MavenPlugin(groupId, artifactId));
	}

}
