/*
 * Copyright 2012-2024 the original author or authors.
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
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Application properties.
 *
 * @author Moritz Halbritter
 */
public class ApplicationProperties {

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

	void writeTo(PrintWriter writer) {
		for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
			writer.printf("%s=%s%n", entry.getKey(), entry.getValue());
		}
	}

	private void add(String key, Object value) {
		Assert.state(!this.properties.containsKey(key), () -> "Property '%s' already exists".formatted(key));
		this.properties.put(key, value);
	}

}
