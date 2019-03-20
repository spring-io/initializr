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

import java.util.Objects;

/**
 * A version reference to either a property or an actual version.
 *
 * @author Stephane Nicoll
 */
public final class VersionReference {

	private final VersionProperty property;

	private final String value;

	private VersionReference(VersionProperty property, String value) {
		this.property = property;
		this.value = value;
	}

	public static VersionReference ofProperty(VersionProperty property) {
		return new VersionReference(property, null);
	}

	public static VersionReference ofProperty(String internalProperty) {
		return ofProperty(VersionProperty.of(internalProperty));
	}

	public static VersionReference ofValue(String value) {
		return new VersionReference(null, value);
	}

	/**
	 * Specify if this reference defines a property.
	 * @return {@code true} if this version is backed by a property
	 */
	public boolean isProperty() {
		return this.property != null;
	}

	/**
	 * Return the {@link VersionProperty} or {@code null} if this reference is not a
	 * property.
	 * @return the version property or {@code null}
	 */
	public VersionProperty getProperty() {
		return this.property;
	}

	/**
	 * Return the version of {@code null} if this reference is backed by a property.
	 * @return the version or {@code null}
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VersionReference that = (VersionReference) o;
		return Objects.equals(this.property, that.property)
				&& Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.property, this.value);
	}

	@Override
	public String toString() {
		return (this.property != null) ? "${" + this.property.toStandardFormat() + "}"
				: this.value;
	}

}
