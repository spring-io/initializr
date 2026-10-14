/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.configuration.format.yaml.YamlFormat;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import org.springframework.util.Assert;

/**
 * A {@link ProjectContributor} that contributes {@code application[-{profile}].yaml}
 * files to a project, one per source set and Spring profile that has properties. The
 * {@code application.yaml} of the main source set is contributed even when empty. Dotted
 * property names are written as nested YAML keys, so a key can't be a value and a nested
 * map at the same time.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
public class ApplicationYamlPropertiesContributor extends AbstractApplicationPropertiesContributor {

	private static final String YAML_SPACE = "  ";

	/**
	 * Creates a new instance.
	 * @param properties the application properties to contribute
	 * @param description the description of the project, used to resolve the source
	 * structures
	 */
	public ApplicationYamlPropertiesContributor(ApplicationProperties properties, ProjectDescription description) {
		super(properties, description, new YamlFormat());
	}

	@Override
	protected void writeProperties(Map<String, Object> properties, PrintWriter writer) {
		writeAll(toNestedMap(properties), writer, 0);
	}

	/**
	 * Turns dotted property names into nested maps, for example {@code a.b=1} into
	 * {@code {a={b=1}}}.
	 * @param properties the properties to convert
	 * @return the nested properties
	 */
	private static Map<String, Object> toNestedMap(Map<String, Object> properties) {
		// Keeps insertion order, so that the generated files are reproducible
		Map<String, Object> nested = new LinkedHashMap<>();
		properties.forEach((key, value) -> insertValueAtPath(nested, key.split("\\."), value));
		return nested;
	}

	@SuppressWarnings("unchecked")
	private static void insertValueAtPath(Map<String, Object> map, String[] path, Object value) {
		Map<String, Object> current = map;
		for (int i = 0; i < path.length - 1; i++) {
			String segment = path[i];
			Object child = current.computeIfAbsent(segment, (k) -> new LinkedHashMap<>());
			Assert.state(child instanceof Map, () -> conflict(segment));
			current = (Map<String, Object>) child;
		}
		String leaf = path[path.length - 1];
		Assert.state(!(current.get(leaf) instanceof Map), () -> conflict(leaf));
		current.put(leaf, value);
	}

	private static String conflict(String key) {
		return "Property '%s' can't be a value and a nested map at the same time".formatted(key);
	}

	private static void writeAll(Map<String, Object> map, PrintWriter writer, int indent) {
		map.entrySet().forEach((entry) -> writeEntry(entry, writer, indent));
	}

	@SuppressWarnings("unchecked")
	private static void writeEntry(Map.Entry<String, Object> entry, PrintWriter writer, int indent) {
		String indentStr = YAML_SPACE.repeat(indent);
		Object value = entry.getValue();

		if (value instanceof Map<?, ?> nestedMap) {
			writer.printf("%s%s:%n", indentStr, entry.getKey());
			writeAll((Map<String, Object>) nestedMap, writer, indent + 1);
			return;
		}

		if (value instanceof Collection<?> collection) {
			if (collection.isEmpty()) {
				writer.printf("%s%s: []%n", indentStr, entry.getKey());
				return;
			}
			writer.printf("%s%s:%n", indentStr, entry.getKey());
			writeCollection(collection, writer, indent + 1);
			return;
		}

		writer.printf("%s%s: %s%n", indentStr, entry.getKey(), value);
	}

	private static void writeCollection(Collection<?> collection, PrintWriter writer, int indent) {
		String indentStr = YAML_SPACE.repeat(indent);
		collection.forEach((element) -> writer.printf("%s- %s%n", indentStr, element));
	}

}
