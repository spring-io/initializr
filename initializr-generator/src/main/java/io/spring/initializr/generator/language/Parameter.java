/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.language;

import java.util.function.Consumer;

/**
 * A parameter, typically of a method or function.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public final class Parameter implements Annotatable {

	private final String name;

	private final String type;

	private final AnnotationContainer annotations;

	private Parameter(Builder builder) {
		this.name = builder.name;
		this.type = builder.type;
		this.annotations = builder.annotations.deepCopy();
	}

	/**
	 * Create a parameter with the specified name and type.
	 * @param name the name of the parameter
	 * @param type the type
	 * @return a parameter
	 */
	public static Parameter of(String name, String type) {
		return new Builder(name).type(type).build();
	}

	/**
	 * Create a parameter with the specified name and {@link ClassName type}.
	 * @param name the name of the parameter
	 * @param type the type
	 * @return a parameter
	 */
	public static Parameter of(String name, ClassName type) {
		return new Builder(name).type(type).build();
	}

	/**
	 * Create a parameter with the specified name and {@link Class type}.
	 * @param name the name of the parameter
	 * @param type the type
	 * @return a parameter
	 */
	public static Parameter of(String name, Class<?> type) {
		return new Builder(name).type(type).build();
	}

	/**
	 * Initialize a builder for a parameter with the specified name.
	 * @param name the name of the parameter
	 * @return a builder to further configure the parameter
	 */
	public static Builder builder(String name) {
		return new Builder(name);
	}

	/**
	 * Return the name of the parameter.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the typ of the parameter.
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	@Override
	public AnnotationContainer annotations() {
		return this.annotations;
	}

	/**
	 * Builder for creating a {@link Parameter}.
	 */
	public static class Builder {

		private final String name;

		private String type;

		private final AnnotationContainer annotations = new AnnotationContainer();

		Builder(String name) {
			this.name = name;
		}

		/**
		 * Specify the {@link ClassName type} of the parameter.
		 * @param type the type
		 * @return this for method chaining
		 */
		public Builder type(ClassName type) {
			return type(type.getName());
		}

		/**
		 * Specify the {@link Class type} of the parameter.
		 * @param type the type
		 * @return this for method chaining
		 */
		public Builder type(Class<?> type) {
			return type(type.getCanonicalName());
		}

		/**
		 * Specify the type of the parameter.
		 * @param type the type
		 * @return this for method chaining
		 */
		public Builder type(String type) {
			this.type = type;
			return this;
		}

		/**
		 * Annotate the parameter with the specified annotation.
		 * @param className the class of the annotation
		 * @return this for method chaining
		 */
		public Builder annotate(ClassName className) {
			return annotate(className, null);
		}

		/**
		 * Annotate the parameter with the specified annotation, customized by the
		 * specified consumer.
		 * @param className the class of the annotation
		 * @param annotation a consumer of the builder
		 * @return this for method chaining
		 */
		public Builder annotate(ClassName className, Consumer<Annotation.Builder> annotation) {
			this.annotations.add(className, annotation);
			return this;
		}

		public Parameter build() {
			return new Parameter(this);
		}

	}

}
