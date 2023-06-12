/*
 * Copyright 2012-2023 the original author or authors.
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
 * A container for {@link MavenExtension maven extensions}.
 *
 * @author Niklas Herder
 * @author Stephane Nicoll
 */
public class MavenExtensionContainer {

	private final Map<String, MavenExtension.Builder> extensions = new LinkedHashMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenExtension} is added
	 */
	public boolean isEmpty() {
		return this.extensions.isEmpty();
	}

	/**
	 * Specify if this container has a extension with the specified {@code groupId} and
	 * {@code artifactId}.
	 * @param groupId the groupId of the extension
	 * @param artifactId the artifactId of the extension
	 * @return {@code true} if an item with the specified {@code groupId} and
	 * {@code artifactId} exists
	 */
	public boolean has(String groupId, String artifactId) {
		return this.extensions.containsKey(extensionKey(groupId, artifactId));
	}

	/**
	 * Returns a {@link Stream} of registered {@link MavenExtension}s.
	 * @return a stream of {@link MavenExtension}s
	 */
	public Stream<MavenExtension> values() {
		return this.extensions.values().stream().map(MavenExtension.Builder::build);
	}

	/**
	 * Add a {@link MavenExtension} with the specified {@code groupId},
	 * {@code artifactId}, and {@code version}. If the extension has already been added,
	 * only update the version if necessary.
	 * @param groupId the groupId of the extension
	 * @param artifactId the artifactId of the extension
	 * @param version the version of the extension
	 * @see #add(String, String, Consumer)
	 */
	public void add(String groupId, String artifactId, String version) {
		add(groupId, artifactId, (extension) -> extension.version(version));
	}

	/**
	 * Add a {@link MavenExtension} with the specified {@code groupId} and
	 * {@code artifactId} and {@link Consumer} to customize the extension. If the
	 * extension has already been added, the consumer can be used to further tune the
	 * existing extension configuration.
	 * @param groupId the groupId of the extension
	 * @param artifactId the artifactId of the extension
	 * @param extension a {@link Consumer} to customize the {@link MavenExtension}
	 */
	public void add(String groupId, String artifactId, Consumer<MavenExtension.Builder> extension) {
		extension.accept(addExtension(groupId, artifactId));
	}

	private MavenExtension.Builder addExtension(String groupId, String artifactId) {
		return this.extensions.computeIfAbsent(extensionKey(groupId, artifactId),
				(extensionId) -> new MavenExtension.Builder(groupId, artifactId));
	}

	/**
	 * Remove the extension with the specified {@code groupId} and {@code artifactId}.
	 * @param groupId the groupId of the extension to remove
	 * @param artifactId the artifactId of the extension to remove
	 * @return {@code true} if such a extension was registered, {@code false} otherwise
	 */
	public boolean remove(String groupId, String artifactId) {
		return this.extensions.remove(extensionKey(groupId, artifactId)) != null;
	}

	private String extensionKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

}
