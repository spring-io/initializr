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

package io.spring.initializr.generator.buildsystem;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import io.spring.initializr.generator.version.VersionProperty;

/**
 * A container for properties. Arbitrary properties can be specified as well as ones that
 * refer to a {@link VersionProperty version}.
 *
 * @author Stephane Nicoll
 */
public class PropertyContainer {

	private final Map<String, String> properties = new TreeMap<>();

	private final Map<VersionProperty, String> versions = new TreeMap<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no property is registered
	 */
	public boolean isEmpty() {
		return this.properties.isEmpty() && this.versions.isEmpty();
	}

	/**
	 * Specify if this container has a property with the specified name.
	 * @param name the name of a property
	 * @return {@code true} if a property with the specified {@code name} is registered
	 */
	public boolean has(String name) {
		return this.properties.containsKey(name)
				|| this.versions.keySet().stream().anyMatch((ref) -> ref.toStandardFormat().equals(name));
	}

	/**
	 * Register a property with the specified {@code name} and {@code value}. If a
	 * property with that {@code name} already exists, its value is overridden by the
	 * specified {@code value}.
	 * @param name the name of a property
	 * @param value the value of the property
	 * @return this container
	 * @see #version(String, String) to register a property that refers to a version
	 */
	public PropertyContainer property(String name, String value) {
		this.properties.put(name, value);
		return this;
	}

	/**
	 * Register a property that refers to the specified {@link VersionProperty version}.
	 * If a version with the same {@link VersionProperty} already exists, its value is
	 * overridden by the specified {@code version}.
	 * @param versionProperty the name of a version
	 * @param version the version
	 * @return this container
	 */
	public PropertyContainer version(VersionProperty versionProperty, String version) {
		this.versions.put(versionProperty, version);
		return this;
	}

	/**
	 * Register a public {@link VersionProperty} with the specified {@code name}. If a
	 * version with the same {@code name} already exists, its value is overridden by the
	 * specified {@code version}.
	 * @param name the public name of a version
	 * @param version the version
	 * @return this container
	 * @see VersionProperty#isInternal()
	 */
	public PropertyContainer version(String name, String version) {
		return version(VersionProperty.of(name, false), version);
	}

	/**
	 * Return the registered properties. Does not contain registered versions.
	 * @return the property entries
	 */
	public Stream<Entry<String, String>> values() {
		return this.properties.entrySet().stream();
	}

	/**
	 * Return the registered versions using the specified {@code nameFactory}.
	 * @param nameFactory the factory to use to generate a version name based on a
	 * {@link VersionProperty}
	 * @return the version entries.
	 */
	public Stream<Entry<String, String>> versions(Function<VersionProperty, String> nameFactory) {
		return this.versions.entrySet().stream()
				.map((entry) -> new SimpleEntry<>(nameFactory.apply(entry.getKey()), entry.getValue()));
	}

}
