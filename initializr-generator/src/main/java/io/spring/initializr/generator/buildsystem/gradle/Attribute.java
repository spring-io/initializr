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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.Objects;

/**
 * An attribute.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
public class Attribute {

	private final String name;

	private final String value;

	private final Type type;

	protected Attribute(String name, String value, Type type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	/**
	 * Create an attribute that {@linkplain Type#SET sets} the specified value.
	 * @param name the name of the attribute
	 * @param value the value to set
	 * @return an attribute
	 */
	public static Attribute set(String name, String value) {
		return new Attribute(name, value, Type.SET);
	}

	/**
	 * Create an attribute that {@linkplain Type#APPEND appends} the specified value.
	 * @param name the name of the attribute
	 * @param value the value to append
	 * @return an attribute
	 */
	public static Attribute append(String name, String value) {
		return new Attribute(name, value, Type.APPEND);
	}

	/**
	 * Return the name of the attribute.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the value of the attribute to set or to append.
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Return the {@link Type} of the attribute.
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Attribute attribute = (Attribute) o;
		return Objects.equals(this.name, attribute.name) && Objects.equals(this.value, attribute.value)
				&& this.type == attribute.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.value, this.type);
	}

	@Override
	public String toString() {
		return this.name + ((this.type == Type.SET) ? " = " : " += ") + this.value;
	}

	public enum Type {

		/**
		 * Set the value of the attribute.
		 */
		SET,

		/**
		 * Append the value to the attribute.
		 */
		APPEND

	}

}
