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

package io.spring.initializr.generator.language.java;

import java.util.function.Supplier;

/**
 * Primitives for the java language.
 *
 * @author Matt Berteaux
 */
public final class JavaPrimitives {

	private JavaPrimitives() {
		// hide public constructor
	}

	public static JavaPrimitive byteValue(Byte value) {
		return new JavaByte(value);
	}

	public static JavaPrimitive shortValue(Short value) {
		return new JavaShort(value);
	}

	public static JavaPrimitive integerValue(Integer value) {
		return new JavaInteger(value);
	}

	public static JavaPrimitive longValue(Long value) {
		return new JavaLong(value);
	}

	public static JavaPrimitive doubleValue(Double value) {
		return new JavaDouble(value);
	}

	public static JavaPrimitive floatValue(Float value) {
		return new JavaFloat(value);
	}

	public static JavaPrimitive charValue(String charValue) {
		return new JavaChar(charValue);
	}

	public static JavaPrimitive booleanValue(Boolean value) {
		return new JavaBoolean(value);
	}

	private static String valueOrNull(Object value, Supplier<String> nonNullSupplier) {
		if (value == null) {
			return "null";
		}
		return nonNullSupplier.get();
	}

	public static final class JavaByte implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Byte";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "byte";

		protected final Byte value;

		private JavaByte(Byte value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Byte.toString(this.value));
		}

	}

	public static final class JavaShort implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Short";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "short";

		protected final Short value;

		private JavaShort(Short value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Short.toString(this.value));
		}

	}

	public static final class JavaInteger implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Integer";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "int";

		protected final Integer value;

		private JavaInteger(Integer value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Integer.toString(this.value));
		}

	}

	public static final class JavaLong implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Long";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "long";

		protected final Long value;

		private JavaLong(Long value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "L");
		}

	}

	public static final class JavaDouble implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Double";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "double";

		protected final Double value;

		private JavaDouble(Double value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Double.toString(this.value));
		}

	}

	public static final class JavaFloat implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Float";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "float";

		protected final Float value;

		private JavaFloat(Float value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "f");
		}

	}

	public static final class JavaChar implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Character";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "char";

		protected final String value;

		private JavaChar(String charString) {
			this.value = charString;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value);
		}

	}

	public static final class JavaBoolean implements JavaPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "java.lang.Boolean";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "boolean";

		protected final Boolean value;

		private JavaBoolean(Boolean value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Boolean.toString(this.value));
		}

	}

}
