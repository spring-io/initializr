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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application properties.
 *
 * @author Moritz Halbritter
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

	void writeProperties(PrintWriter writer) {
		for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
			writer.printf("%s=%s%n", entry.getKey(), (entry.getValue() instanceof List)
					? ((List<?>) entry.getValue()).stream().map(Object::toString).collect(Collectors.joining(", "))
					: entry.getValue());
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
			if (value instanceof List) {
				writer.printf("%s%s:%n", indentStr, entry.getKey());
				writeList((List<?>) value, writer, indent + 1);
			}
			else {
				writer.printf("%s%s: %s%n", indentStr, entry.getKey(), value);
			}
		}
	}

	private static void writeList(List<?> list, PrintWriter writer, int indent) {
		String indentStr = YAML_SPACE.repeat(indent);
		list.forEach((element) -> writer.printf("%s- %s%n", indentStr, element));
	}

	private void add(String key, Object value) {
		this.properties.merge(key, value, (oldValue, newValue) -> {
			var newValues = new ArrayList<>((oldValue instanceof List) ? ((List<?>) oldValue).size() + 1 : 2);
			if (oldValue instanceof List<?>) {
				newValues.addAll((List<?>) oldValue);
			}
			else {
				newValues.add(oldValue);
			}
			if (!newValues.contains(newValue)) {
				newValues.add(newValue);
				return newValues;
			}
			return oldValue;
		});
	}

}
