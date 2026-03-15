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
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Application properties.
 *
 * @author Moritz Halbritter
 * @author Rodrigo Mibielli Peixoto
 */
public class ApplicationProperties {

	private static final String YAML_SPACE = "  ";

	private final Map<String, Object> properties = new HashMap<>();

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
	 * Tests if the specified key exists.
	 * @param key the key of the property.
	 * @return true if the key exists.
	 */
	public boolean contains(String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * Returns the value cast to the class associated to the key.
	 * @param <T> the type of the returned value.
	 * @param key the associated key.
	 * @param clazz the class or interface to cast the value.
	 * @return the corresponding value cast or null if there is no mapping for the key.
	 * @throws ClassCastException – if the object is not null and is not assignable to the
	 * type T.
	 */
	public @Nullable <T> T get(String key, Class<T> clazz) {
		return clazz.cast(get(key));
	}

	/**
	 * Returns the value associated to the key.
	 * @param key the associated key.
	 * @return the corresponding value or null if there is no mapping for the key.
	 */
	public @Nullable Object get(String key) {
		return this.properties.get(key);
	}

	/**
	 * Removes the key (and its corresponding value) if it exists.
	 * @param key the key that needs to be removed.
	 * @return true if the key (and its corresponding value) has been removed.
	 */
	public boolean remove(String key) {
		return this.properties.remove(key) != null;
	}

	/**
	 * Adds a new property.
	 * @param key the key of the property
	 * @param value the value of the property
	 */
	public void add(String key, Collection<?> value) {
		add(key, (Object) value);
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

}
