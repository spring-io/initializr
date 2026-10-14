/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * The properties written to a single {@code application[-{profile}]} file, keyed by their
 * dotted name. The file extension depends on the requested
 * {@link io.spring.initializr.generator.configuration.format.ConfigurationFileFormat}.
 *
 * @author Moritz Halbritter
 * @author Rodrigo Mibielli Peixoto
 */
public class ApplicationPropertiesFile {

	private final Map<String, Object> properties = new LinkedHashMap<>();

	/**
	 * Adds a property.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, long value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a property.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, boolean value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a property.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, double value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a property.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, String value) {
		add(key, (Object) value);
	}

	/**
	 * Adds a property.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, Collection<?> value) {
		add(key, (Object) value);
	}

	/**
	 * Whether the given key exists.
	 * @param key the property key
	 * @return true if the key exists
	 */
	public boolean contains(String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * Returns the value of the given key, cast to the given type.
	 * @param <T> the type of the value
	 * @param key the property key
	 * @param clazz the type of the value
	 * @return the value, or null if the key does not exist
	 * @throws ClassCastException if the value is not assignable to the type T
	 */
	public <T> @Nullable T get(String key, Class<T> clazz) {
		return clazz.cast(get(key));
	}

	/**
	 * Returns the value of the given key.
	 * @param key the property key
	 * @return the value, or null if the key does not exist
	 */
	public @Nullable Object get(String key) {
		return this.properties.get(key);
	}

	/**
	 * Removes the given key and its value.
	 * @param key the property key
	 * @return true if the key has been removed
	 */
	public boolean remove(String key) {
		return this.properties.remove(key) != null;
	}

	boolean isEmpty() {
		return this.properties.isEmpty();
	}

	Map<String, Object> properties() {
		return Collections.unmodifiableMap(this.properties);
	}

	private void add(String key, Object value) {
		Assert.state(!this.properties.containsKey(key), () -> "Property '%s' already exists".formatted(key));
		this.properties.put(key, value);
	}

}
