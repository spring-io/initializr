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

package io.spring.initializr.generator.language.kotlin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.AnnotationContainer;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.CodeBlock;

/**
 * Declaration of a property written in Kotlin.
 *
 * @author Matt Berteaux
 */
public final class KotlinPropertyDeclaration implements Annotatable {

	private final AnnotationContainer annotations = new AnnotationContainer();

	private final boolean isVal;

	private final String name;

	private final String returnType;

	private final List<KotlinModifier> modifiers;

	private final CodeBlock valueCode;

	private final Accessor getter;

	private final Accessor setter;

	private KotlinPropertyDeclaration(Builder<?> builder) {
		this.name = builder.name;
		this.returnType = builder.returnType;
		this.modifiers = new ArrayList<>(builder.modifiers);
		this.isVal = builder.isVal;
		this.valueCode = builder.valueCode;
		this.getter = builder.getter;
		this.setter = builder.setter;
	}

	/**
	 * Returns a builder for a {@code val} property with the given name.
	 * @param name the name
	 * @return the builder
	 */
	public static ValBuilder val(String name) {
		return new ValBuilder(name);
	}

	/**
	 * Returns a builder for a {@code var} property with the given name.
	 * @param name the name
	 * @return the builder
	 */
	public static VarBuilder var(String name) {
		return new VarBuilder(name);
	}

	boolean isVal() {
		return this.isVal;
	}

	String getName() {
		return this.name;
	}

	String getReturnType() {
		return this.returnType;
	}

	/**
	 * Returns the modifiers.
	 * @return the modifiers
	 */
	public List<KotlinModifier> getModifiers() {
		return this.modifiers;
	}

	CodeBlock getValueCode() {
		return this.valueCode;
	}

	Accessor getGetter() {
		return this.getter;
	}

	Accessor getSetter() {
		return this.setter;
	}

	@Override
	public AnnotationContainer annotations() {
		return this.annotations;
	}

	/**
	 * Builder for creating a {@link KotlinPropertyDeclaration}.
	 *
	 * @param <T> a {@link Builder} subclass.
	 */
	public abstract static class Builder<T extends Builder<T>> {

		private final boolean isVal;

		private final String name;

		private String returnType;

		private List<KotlinModifier> modifiers = new ArrayList<>();

		private CodeBlock valueCode;

		private Accessor getter;

		private Accessor setter;

		private Builder(String name, boolean isVal) {
			this.name = name;
			this.isVal = isVal;
		}

		/**
		 * Returns {@code this} instance.
		 * @return this instance
		 */
		protected abstract T self();

		/**
		 * Returns the getter.
		 * @return the getter
		 */
		@SuppressWarnings("unchecked")
		public AccessorBuilder<T> getter() {
			return new AccessorBuilder<>((T) this, (created) -> this.getter = created);
		}

		/**
		 * Returns the setter.
		 * @return setter
		 */
		@SuppressWarnings("unchecked")
		public AccessorBuilder<T> setter() {
			return new AccessorBuilder<>((T) this, (created) -> this.setter = created);
		}

		/**
		 * Sets the return type.
		 * @param returnType the return type
		 * @return this for method chaining
		 */
		public T returning(String returnType) {
			this.returnType = returnType;
			return self();
		}

		/**
		 * Sets the modifiers.
		 * @param modifiers the modifiers
		 * @return this for method chaining
		 */
		public T modifiers(KotlinModifier... modifiers) {
			this.modifiers = Arrays.asList(modifiers);
			return self();
		}

		/**
		 * Sets no value.
		 * @return the property declaration
		 */
		public KotlinPropertyDeclaration emptyValue() {
			return new KotlinPropertyDeclaration(this);
		}

		/**
		 * Sets the given value.
		 * @param valueCode the code for the value
		 * @return the property declaration
		 */
		public KotlinPropertyDeclaration value(CodeBlock valueCode) {
			this.valueCode = valueCode;
			return new KotlinPropertyDeclaration(this);
		}

	}

	/**
	 * Builder for {@code val} properties.
	 */
	public static final class ValBuilder extends Builder<ValBuilder> {

		private ValBuilder(String name) {
			super(name, true);
		}

		@Override
		protected ValBuilder self() {
			return this;
		}

	}

	/**
	 * Builder for {@code val} properties.
	 */
	public static final class VarBuilder extends Builder<VarBuilder> {

		private VarBuilder(String name) {
			super(name, false);
		}

		/**
		 * Sets no value.
		 * @return the property declaration
		 */
		public KotlinPropertyDeclaration empty() {
			return new KotlinPropertyDeclaration(this);
		}

		@Override
		protected VarBuilder self() {
			return this;
		}

	}

	/**
	 * Builder for a property accessor.
	 *
	 * @param <T> the type of builder
	 */
	public static final class AccessorBuilder<T extends Builder<T>> {

		private final AnnotationContainer annotations = new AnnotationContainer();

		private CodeBlock code;

		private final T parent;

		private final Consumer<Accessor> accessorFunction;

		private AccessorBuilder(T parent, Consumer<Accessor> accessorFunction) {
			this.parent = parent;
			this.accessorFunction = accessorFunction;
		}

		/**
		 * Adds an annotation.
		 * @param className the class name of the annotation
		 * @return this for method chaining
		 */
		public AccessorBuilder<?> withAnnotation(ClassName className) {
			return withAnnotation(className, null);
		}

		/**
		 * Adds an annotation.
		 * @param className the class name of the annotation
		 * @param annotation configurer for the annotation
		 * @return this for method chaining
		 */
		public AccessorBuilder<?> withAnnotation(ClassName className, Consumer<Annotation.Builder> annotation) {
			this.annotations.add(className, annotation);
			return this;
		}

		/**
		 * Sets the body.
		 * @param code the code for the body
		 * @return this for method chaining
		 */
		public AccessorBuilder<?> withBody(CodeBlock code) {
			this.code = code;
			return this;
		}

		/**
		 * Builds the accessor.
		 * @return the parent getter / setter
		 */
		public T buildAccessor() {
			this.accessorFunction.accept(new Accessor(this));
			return this.parent;
		}

	}

	static final class Accessor implements Annotatable {

		private final AnnotationContainer annotations;

		private final CodeBlock code;

		Accessor(AccessorBuilder<?> builder) {
			this.annotations = builder.annotations.deepCopy();
			this.code = builder.code;
		}

		CodeBlock getCode() {
			return this.code;
		}

		@Override
		public AnnotationContainer annotations() {
			return this.annotations;
		}

	}

}
