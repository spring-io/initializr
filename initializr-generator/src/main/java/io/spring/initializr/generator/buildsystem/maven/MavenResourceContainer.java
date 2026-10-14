/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A container for {@link MavenResource}s.
 *
 * @author Stephane Nicoll
 */
public class MavenResourceContainer {

	private final Map<String, MavenResource.Builder> resources = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenResource} is added
	 */
	public boolean isEmpty() {
		return this.resources.isEmpty();
	}

	/**
	 * Specify if this container has a resource the specified {@code directory}.
	 * @param directory the resource directory
	 * @return {@code true} if an item for the specified {@code directory} exists
	 */
	public boolean has(String directory) {
		return this.resources.containsKey(directory);
	}

	/**
	 * Returns a {@link Stream} of registered {@link MavenResource}s.
	 * @return a stream of {@link MavenResource}s
	 */
	public Stream<MavenResource> values() {
		return this.resources.values().stream().map(MavenResource.Builder::build);
	}

	/**
	 * Add a resource with default settings for the specified {@code directory}.
	 * @param directory the directory to add
	 */
	public void add(String directory) {
		this.resources.computeIfAbsent(directory, (key) -> new MavenResource.Builder(directory));
	}

	/**
	 * Add a resource with default settings for the specified {@code directory} and
	 * {@link Consumer} to customize the resource. If the resource has already been added,
	 * the consumer can be used to further tune the existing resource configuration.
	 * @param directory the directory to add
	 * @param resource a {@link Consumer} to customize the {@link MavenResource}
	 */
	public void add(String directory, Consumer<MavenResource.Builder> resource) {
		resource.accept(this.resources.computeIfAbsent(directory, (key) -> new MavenResource.Builder(directory)));
	}

	/**
	 * Remove the resource with the specified {@code directory}.
	 * @param directory the directory to remove
	 * @return {@code true} if such a resource was registered, {@code false} otherwise
	 */
	public boolean remove(String directory) {
		return this.resources.remove(directory) != null;
	}

}
