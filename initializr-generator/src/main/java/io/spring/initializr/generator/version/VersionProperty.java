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

package io.spring.initializr.generator.version;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.util.StringUtils;

/**
 * Represents a valid property for a version. A property must be lower case and can define
 * a dot or an hyphen to separate words. For instance, "foo-acme.version",
 * "foo.acme.version" or "foo-acme-version" are valid properties.
 *
 * @author Stephane Nicoll
 */
public final class VersionProperty implements Serializable, Comparable<VersionProperty> {

	private static final List<Character> SUPPORTED_CHARS = Arrays.asList('.', '-');

	private final String property;

	private final boolean internal;

	private VersionProperty(String property, boolean internal) {
		this.property = validateFormat(property);
		this.internal = internal;
	}

	/**
	 * Create a {@link VersionProperty}.
	 * @param property the name of the property
	 * @param internal whether the property is internal and can be tuned according to the
	 * build system
	 * @return a version property
	 */
	public static VersionProperty of(String property, boolean internal) {
		return new VersionProperty(property, internal);
	}

	/**
	 * Create an internal {@link VersionProperty}.
	 * @param property the name of the property
	 * @return a version property whose format can be tuned according to the build system
	 */
	public static VersionProperty of(String property) {
		return of(property, true);
	}

	/**
	 * Specify if the property is internally defined and can be tuned according to the
	 * build system.
	 * @return {@code true} if the property is defined within the scope of this project
	 */
	public boolean isInternal() {
		return this.internal;
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VersionProperty that = (VersionProperty) o;
		return this.internal == that.internal && this.property.equals(that.property);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.property, this.internal);
	}

	@Override
	public String toString() {
		return this.property;
	}

}
