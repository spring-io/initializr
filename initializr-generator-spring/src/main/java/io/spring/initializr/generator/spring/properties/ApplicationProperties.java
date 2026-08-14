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

package io.spring.initializr.generator.spring.properties;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Application properties.
 * <p>
 * Properties added directly to this instance belong to the main source set and the
 * default profile, and are written to {@code src/main/resources}. Properties for another
 * source set or Spring profile can be added to the {@link #section(SourceSet, String)
 * matching section}.
 *
 * @author Moritz Halbritter
 * @author Rodrigo Mibielli Peixoto
 */
public class ApplicationProperties {

	private static final String YAML_SPACE = "  ";

	private final Map<String, Object> properties = new HashMap<>();

	private final Map<SectionKey, ApplicationProperties> sections = new LinkedHashMap<>();

	private final @Nullable ApplicationProperties root;

	/**
	 * Creates the application properties of the main source set and the default profile.
	 */
	public ApplicationProperties() {
		this(null);
	}

	private ApplicationProperties(@Nullable ApplicationProperties root) {
		this.root = root;
	}

	/**
	 * Returns the application properties for the given source set and profile, creating
	 * the section if necessary. Calling this method with {@link SourceSet#MAIN} and a
	 * {@code null} profile returns the root application properties. Can be called on a
	 * section as well as on the root application properties; both resolve to the same
	 * section.
	 * @param sourceSet the source set the properties belong to
	 * @param profile the Spring profile the properties belong to, or {@code null} for the
	 * default profile
	 * @return the application properties for the given source set and profile
	 * @throws IllegalArgumentException if the profile is not a plain profile name
	 */
	public ApplicationProperties section(SourceSet sourceSet, @Nullable String profile) {
		Assert.notNull(sourceSet, "'sourceSet' must not be null");
		if (this.root != null) {
			return this.root.section(sourceSet, profile);
		}
		if (profile != null) {
			Assert.hasText(profile, "'profile' must not be empty");
			Assert.isTrue(isValidProfile(profile),
					() -> "'profile' must be a plain profile name, but was '%s'".formatted(profile));
		}
		if (sourceSet == SourceSet.MAIN && profile == null) {
			return this;
		}
		return this.sections.computeIfAbsent(new SectionKey(sourceSet, profile),
				(key) -> new ApplicationProperties(this));
	}

	/**
	 * Returns the application properties for the given source set and the default
	 * profile, creating the section if necessary.
	 * @param sourceSet the source set the properties belong to
	 * @return the application properties for the given source set
	 */
	public ApplicationProperties section(SourceSet sourceSet) {
		return section(sourceSet, null);
	}

	private static boolean isValidProfile(String profile) {
		return profile.indexOf('/') < 0 && profile.indexOf('\\') < 0 && !profile.startsWith(".");
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, long value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, boolean value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, double value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, String value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, Collection<?> value) {
		add(key, (Object) value);
	}

	/**
	 * Tests if the specified key exists.
	 * @param key the key of the property
	 * @return true if the key exists
	 */
	public boolean contains(String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * Returns the value cast to the class associated to the key.
	 * @param <T> the type of the returned value
	 * @param key the associated key
	 * @param clazz the class or interface to cast the value
	 * @return the corresponding value cast or null if there is no mapping for the key
	 * @throws ClassCastException if the object is not null and is not assignable to the
	 * type T
	 */
	public <T> @Nullable T get(String key, Class<T> clazz) {
		return clazz.cast(get(key));
	}

	/**
	 * Returns the value associated to the key.
	 * @param key the associated key
	 * @return the corresponding value or null if there is no mapping for the key
	 */
	public @Nullable Object get(String key) {
		return this.properties.get(key);
	}

	/**
	 * Removes the key (and its corresponding value) if it exists.
	 * @param key the key that needs to be removed
	 * @return true if the key (and its corresponding value) has been removed
	 */
	public boolean remove(String key) {
		return this.properties.remove(key) != null;
	}

	void writeProperties(PrintWriter writer) {
		for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
			Object value = (entry.getValue() instanceof Collection<?> collection)
					? StringUtils.collectionToCommaDelimitedString(collection) : entry.getValue();
			writer.printf("%s=%s%n", entry.getKey(), value);
		}
	}

	void writeYaml(PrintWriter writer) {
		Map<String, Object> nested = flattenToNestedMap(this.properties);
		writeYamlRecursive(nested, writer, 0);
	}

	private static Map<String, Object> flattenToNestedMap(Map<String, Object> flatMap) {
		Map<String, Object> nested = new HashMap<>();
		flatMap.forEach((key, value) -> {
			String[] path = parseKeyPath(key);
			insertValueAtPath(nested, path, value);
		});
		return nested;
	}

	private static String[] parseKeyPath(String key) {
		return key.split("\\.");
	}

	@SuppressWarnings("unchecked")
	private static void insertValueAtPath(Map<String, Object> map, String[] path, Object value) {
		Map<String, Object> current = map;
		for (int i = 0; i < path.length - 1; i++) {
			String segment = path[i];
			current = (Map<String, Object>) current.computeIfAbsent(segment, (k) -> new HashMap<>());
		}
		current.put(path[path.length - 1], value);
	}

	private static void writeYamlRecursive(Map<String, Object> map, PrintWriter writer, int indent) {
		map.entrySet().forEach((entry) -> writeEntry(entry, writer, indent));
	}

	@SuppressWarnings("unchecked")
	private static void writeEntry(Map.Entry<String, Object> entry, PrintWriter writer, int indent) {
		String indentStr = YAML_SPACE.repeat(indent);
		Object value = entry.getValue();

		if (value instanceof Map<?, ?> nestedMap) {
			writer.printf("%s%s:%n", indentStr, entry.getKey());
			writeYamlRecursive((Map<String, Object>) nestedMap, writer, indent + 1);
		}
		else {
			if (value instanceof Collection<?> collection) {
				if (collection.isEmpty()) {
					writer.printf("%s%s: []%n", indentStr, entry.getKey());
				}
				else {
					writer.printf("%s%s:%n", indentStr, entry.getKey());
					writeCollection(collection, writer, indent + 1);
				}
			}
			else {
				writer.printf("%s%s: %s%n", indentStr, entry.getKey(), value);
			}
		}
	}

	private static void writeCollection(Collection<?> collection, PrintWriter writer, int indent) {
		String indentStr = YAML_SPACE.repeat(indent);
		collection.forEach((element) -> writer.printf("%s- %s%n", indentStr, element));
	}

	private void add(String key, Object value) {
		Assert.state(!this.properties.containsKey(key), () -> "Property '%s' already exists".formatted(key));
		this.properties.put(key, value);
	}

	boolean hasProperties() {
		return !this.properties.isEmpty();
	}

	Map<SectionKey, ApplicationProperties> getSections() {
		return Collections.unmodifiableMap(this.sections);
	}

	/**
	 * The source set and profile a section of application properties belongs to.
	 *
	 * @param sourceSet the source set the properties belong to
	 * @param profile the Spring profile the properties belong to, or {@code null} for the
	 * default profile
	 */
	record SectionKey(SourceSet sourceSet, @Nullable String profile) {
	}

}
