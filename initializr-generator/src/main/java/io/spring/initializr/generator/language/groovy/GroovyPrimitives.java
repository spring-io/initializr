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

package io.spring.initializr.generator.language.groovy;

import java.util.function.Supplier;

import io.spring.initializr.generator.language.java.JavaPrimitives;

/**
 * Primitives for the Groovy language. Are just wrappers around the
 * {@link JavaPrimitives}.
 *
 * @author Matt Berteaux
 */
public final class GroovyPrimitives {

	private GroovyPrimitives() {
		// hide public constructor
	}

	public static GroovyPrimitive byteValue(Byte value) {
		return new GroovyByte(value);
	}

	public static GroovyPrimitive shortValue(Short value) {
		return new GroovyShort(value);
	}

	public static GroovyPrimitive integerValue(Integer value) {
		return new GroovyInteger(value);
	}

	public static GroovyPrimitive doubleValue(Double value) {
		return new GroovyDouble(value);
	}

	public static GroovyPrimitive longValue(Long value) {
		return new GroovyLong(value);
	}

	public static GroovyPrimitive charValue(String charString) {
		return new GroovyChar(charString);
	}

	public static GroovyPrimitive booleanValue(Boolean value) {
		return new GroovyBoolean(value);
	}

	private static String valueOrNull(Object value, Supplier<String> nonNullSupplier) {
		if (value == null) {
			return "null";
		}
		return nonNullSupplier.get();
	}

	public static final class GroovyByte implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Byte";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "byte";

		protected final Byte value;

		public GroovyByte(Byte value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Byte.toString(this.value));
		}

	}

	public static final class GroovyShort implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Short";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "short";

		protected final Short value;

		private GroovyShort(Short value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Short.toString(this.value));
		}

	}

	public static final class GroovyInteger implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Integer";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "int";

		protected final Integer value;

		private GroovyInteger(Integer value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Integer.toString(this.value));
		}

	}

	public static final class GroovyDouble implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Double";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "double";

		protected final Double value;

		private GroovyDouble(Double value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Double.toString(this.value));
		}

	}

	public static final class GroovyFloat implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Float";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "float";

		protected final Float value;

		private GroovyFloat(Float value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "f");
		}

	}

	public static final class GroovyLong implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Long";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "long";

		protected final Long value;

		private GroovyLong(Long value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "L");
		}

	}

	public static final class GroovyChar implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Character";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "char";

		protected final String value;

		private GroovyChar(String charString) {
			this.value = charString;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value);
		}

	}

	public static final class GroovyBoolean implements GroovyPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Boolean";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "boolean";

		protected final Boolean value;

		private GroovyBoolean(Boolean value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Boolean.toString(this.value));
		}

	}

}
