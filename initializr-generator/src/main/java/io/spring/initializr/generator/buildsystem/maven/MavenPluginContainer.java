/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Builder;

/**
 * A container for {@link MavenPlugin}s.
 *
 * @author HaiTao Zhang
 */
public class MavenPluginContainer {

	private final Map<String, MavenPlugin.Builder> plugins = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenPlugin} is added
	 */
	public boolean isEmpty() {
		return this.plugins.isEmpty();
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
		return this.plugins.containsKey(pluginKey(groupId, artifactId));
	}

	/**
	 * Returns a {@link Stream} of registered {@link MavenPlugin}s.
	 * @return a stream of {@link MavenPlugin}s
	 */
	public Stream<MavenPlugin> values() {
		return this.plugins.values().stream().map(Builder::build);
	}

	/**
	 * Add a {@link MavenPlugin} with the specified {@code groupId} and
	 * {@code artifactId}. Does nothing if the plugin has already been added.
	 * @param groupId the groupId of the plugin
	 * @param artifactId the artifactId of the plugin
	 * @see #add(String, String, Consumer)
	 */
	public void add(String groupId, String artifactId) {
		addPlugin(groupId, artifactId);
	}

	/**
	 * Add a {@link MavenPlugin} with the specified {@code groupId} and {@code artifactId}
	 * and {@link Consumer} to customize the plugin. If the plugin has already been added,
	 * the consumer can be used to further tune the existing plugin configuration.
	 * @param groupId the groupId of the plugin
	 * @param artifactId the artifactId of the plugin
	 * @param plugin a {@link Consumer} to customize the {@link MavenPlugin}
	 */
	public void add(String groupId, String artifactId, Consumer<MavenPlugin.Builder> plugin) {
		plugin.accept(addPlugin(groupId, artifactId));
	}

	private MavenPlugin.Builder addPlugin(String groupId, String artifactId) {
		return this.plugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(pluginId) -> new MavenPlugin.Builder(groupId, artifactId));
	}

	/**
	 * Remove the plugin with the specified {@code groupId} and {@code artifactId}.
	 * @param groupId the groupId of the plugin to remove
	 * @param artifactId the artifactId of the plugin to remove
	 * @return {@code true} if such a plugin was registered, {@code false} otherwise
	 */
	public boolean remove(String groupId, String artifactId) {
		return this.plugins.remove(pluginKey(groupId, artifactId)) != null;
	}

	private String pluginKey(String groupId, String artifactId) {
		return "%s:%s".formatted(groupId, artifactId);
	}

}
