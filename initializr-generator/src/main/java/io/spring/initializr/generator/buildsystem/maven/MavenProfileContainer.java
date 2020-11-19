/*
 * Copyright 2012-2020 the original author or authors.
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
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;

/**
 * A container for {@link MavenProfile maven profiles}.
 *
 * @author Stephane Nicoll
 * @author Daniel Andres Pelaez Lopez
 */
public class MavenProfileContainer {

	private final Map<String, MavenProfile> profiles = new LinkedHashMap<>();

	private final BuildItemResolver buildItemResolver;

	/**
	 * Create an instance with the {@link BuildItemResolver} to use.
	 * @param buildItemResolver the build item resolver to use
	 */
	public MavenProfileContainer(BuildItemResolver buildItemResolver) {
		this.buildItemResolver = buildItemResolver;
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no {@link MavenProfile} is added
	 */
	public boolean isEmpty() {
		return this.profiles.isEmpty();
	}

	/**
	 * Specify if this container has a profile with the specified {@code id}.
	 * @param id the id of the profile
	 * @return {@code true} if a profile with the specified {@code id} exists
	 */
	public boolean has(String id) {
		return this.profiles.containsKey(id);
	}

	/**
	 * Return a {@link Stream} of registered profile identifiers.
	 * @return a stream of profile ids
	 */
	public Stream<String> ids() {
		return this.profiles.keySet().stream();
	}

	/**
	 * Returns a {@link Stream} of registered {@link MavenProfile}s.
	 * @return a stream of {@link MavenProfile}s
	 */
	public Stream<MavenProfile> values() {
		return this.profiles.values().stream();
	}

	/**
	 * Return the profile with the specified {@code id}. If no such profile exists a new
	 * profile is created.
	 * @param id the id of the profile
	 * @return the {@link MavenProfile} for that id
	 */
	public MavenProfile id(String id) {
		return this.profiles.computeIfAbsent(id, (key) -> new MavenProfile(id, this.buildItemResolver));
	}

	/**
	 * Remove the profile with the specified {@code id}.
	 * @param id the id of the profile
	 * @return {@code true} if such a profile was registered, {@code false} otherwise
	 */
	public boolean remove(String id) {
		return this.profiles.remove(id) != null;
	}

}
