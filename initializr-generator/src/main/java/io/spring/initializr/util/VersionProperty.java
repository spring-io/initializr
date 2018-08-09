/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

import org.springframework.util.StringUtils;

/**
 * Represents a valid property for a version. A property must be lower case and can define
 * a dot or an hyphen to separate words. For instance, "foo-acme.version",
 * "foo.acme.version" or "foo-acme-version" are valid properties.
 *
 * @author Stephane Nicoll
 */
public class VersionProperty implements Serializable, Comparable<VersionProperty> {

	private static final List<Character> SUPPORTED_CHARS = Arrays.asList('.', '-');

	private final String property;

	public VersionProperty(String property) {
		this.property = validateFormat(property);
	}

	/**
	 * Return a camel cased representation of this instance.
	 * @return the property in camel case format
	 */
	public String toCamelCaseFormat() {
		String[] tokens = this.property.split("\\-|\\.");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			String part = tokens[i];
			if (i > 0) {
				part = StringUtils.capitalize(part);
			}
			sb.append(part);
		}
		return sb.toString();
	}

	@JsonValue
	public String toStandardFormat() {
		return this.property;
	}

	private static String validateFormat(String property) {
		for (char c : property.toCharArray()) {
			if (Character.isUpperCase(c)) {
				throw new IllegalArgumentException("Invalid property '" + property
						+ "', must not contain upper case");
			}
			if (!Character.isLetterOrDigit(c) && !SUPPORTED_CHARS.contains(c)) {
				throw new IllegalArgumentException(
						"Unsupported character '" + c + "' for '" + property + "'");
			}
		}
		return property;
	}

	@Override
	public int compareTo(VersionProperty o) {
		return this.property.compareTo(o.property);
	}

	@Override
	public String toString() {
		return this.property;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		VersionProperty that = (VersionProperty) o;

		return this.property.equals(that.property);
	}

	@Override
	public int hashCode() {
		return this.property.hashCode();
	}

}
