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

package io.spring.initializr.generator.language.kotlin;

import java.util.function.Supplier;

/**
 * Kotlin primitives.
 *
 * @author Matt Berteaux
 */
public final class KotlinPrimitives {

	private KotlinPrimitives() {
		// hide public constructor
	}

	public static KotlinPrimitive byteValue(Byte value) {
		return new KotlinByte(value);
	}

	public static KotlinPrimitive shortValue(Short value) {
		return new KotlinShort(value);
	}

	public static KotlinPrimitive integerValue(Integer value) {
		return new KotlinInt(value);
	}

	public static KotlinPrimitive doubleValue(Double value) {
		return new KotlinDouble(value);
	}

	public static KotlinPrimitive longValue(Long value) {
		return new KotlinLong(value);
	}

	public static KotlinPrimitive floatValue(Float value) {
		return new KotlinFloat(value);
	}

	public static KotlinPrimitive charValue(String charString) {
		return new KotlinChar(charString);
	}

	public static KotlinPrimitive booleanValue(Boolean value) {
		return new KotlinBoolean(value);
	}

	private static String valueOrNull(Object value, Supplier<String> nonNullSupplier) {
		if (value == null) {
			return "null";
		}
		return nonNullSupplier.get();
	}

	public static final class KotlinByte implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_TYPE_CLASS = "kotlin.Byte";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Byte";

		protected final Byte value;

		private KotlinByte(Byte value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Byte.toString(this.value));
		}

	}

	public static final class KotlinShort implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Short";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Short";

		protected final Short value;

		private KotlinShort(Short value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Short.toString(this.value));
		}

	}

	public static final class KotlinInt implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Int";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Int";

		protected final Integer value;

		private KotlinInt(Integer value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Integer.toString(this.value));
		}

	}

	public static final class KotlinDouble implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Double";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Double";

		protected final Double value;

		private KotlinDouble(Double value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Double.toString(this.value));
		}

	}

	public static final class KotlinLong implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Long";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Long";

		protected final Long value;

		private KotlinLong(Long value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "L");
		}

	}

	public static final class KotlinFloat implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Float";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Float";

		protected final Float value;

		private KotlinFloat(Float value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value + "f");
		}

	}

	public static final class KotlinBoolean implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Boolean";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Boolean";

		protected final Boolean value;

		private KotlinBoolean(Boolean value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> Boolean.toString(this.value));
		}

	}

	public static final class KotlinChar implements KotlinPrimitive {

		/**
		 * The class name of the boxed type.
		 */
		public static final String BOXED_CLASS_NAME = "kotlin.Char";

		/**
		 * The name of the unboxed type.
		 */
		public static final String TYPE = "kotlin.Char";

		protected final String value;

		private KotlinChar(String charString) {
			this.value = charString;
		}

		@Override
		public String toString() {
			return valueOrNull(this.value, () -> this.value);
		}

	}

}
