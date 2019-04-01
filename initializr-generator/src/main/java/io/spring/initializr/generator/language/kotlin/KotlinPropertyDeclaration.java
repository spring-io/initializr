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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.Annotation;

/**
 * Declaration of a property in Kotlin.
 *
 * @author Matt Berteaux
 */
public final class KotlinPropertyDeclaration implements Annotatable {

	private final List<Annotation> annotations = new ArrayList<>();

	private final boolean isVal;

	private final String name;

	private final String returnType;

	private final KotlinExpressionStatement valueExpression;

	private final Accessor getter;

	private final Accessor setter;

	private KotlinPropertyDeclaration(Builder builder) {
		this.name = builder.name;
		this.returnType = builder.returnType;
		this.isVal = builder.isVal;
		this.valueExpression = builder.initializerStatement;
		this.getter = builder.getter;
		this.setter = builder.setter;
	}

	public static ValBuilder val(String name) {
		return new ValBuilder(name);
	}

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

	KotlinExpressionStatement getValueExpression() {
		return this.valueExpression;
	}

	Accessor getGetter() {
		return this.getter;
	}

	Accessor getSetter() {
		return this.setter;
	}

	public boolean hasGetter() {
		return getGetter() != null;
	}

	public boolean hasSetter() {
		return getSetter() != null;
	}

	@Override
	public void annotate(Annotation annotation) {
		this.annotations.add(annotation);
	}

	@Override
	public List<Annotation> getAnnotations() {
		return Collections.unmodifiableList(this.annotations);
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

		private KotlinExpressionStatement initializerStatement;

		private Accessor getter;

		private Accessor setter;

		private Builder(String name, boolean isVal) {
			this.name = name;
			this.isVal = isVal;
		}

		protected abstract T self();

		@SuppressWarnings("unchecked")
		public AccessorBuilder getter() {
			return new AccessorBuilder<>((T) this, (created) -> this.getter = created);
		}

		@SuppressWarnings("unchecked")
		public AccessorBuilder setter() {
			return new AccessorBuilder<>((T) this, (created) -> this.setter = created);
		}

		public T returning(String returnType) {
			this.returnType = returnType;
			return self();
		}

		public KotlinPropertyDeclaration emptyValue() {
			return new KotlinPropertyDeclaration(this);
		}

		public KotlinPropertyDeclaration value(KotlinExpression expression) {
			this.initializerStatement = new KotlinExpressionStatement(expression);
			return new KotlinPropertyDeclaration(this);
		}

	}

	public static final class ValBuilder extends Builder<ValBuilder> {

		private ValBuilder(String name) {
			super(name, true);
		}

		@Override
		protected ValBuilder self() {
			return this;
		}

	}

	public static final class VarBuilder extends Builder<VarBuilder> {

		private VarBuilder(String name) {
			super(name, false);
		}

		public KotlinPropertyDeclaration empty() {
			return new KotlinPropertyDeclaration(this);
		}

		@Override
		protected VarBuilder self() {
			return this;
		}

	}

	public static final class AccessorBuilder<T extends Builder<T>> {

		private final List<Annotation> annotations = new ArrayList<>();

		private KotlinExpressionStatement body;

		private boolean isPrivate = false;

		private final T parent;

		private final Consumer<Accessor> accessorFunction;

		private AccessorBuilder(T parent, Consumer<Accessor> accessorFunction) {
			this.parent = parent;
			this.accessorFunction = accessorFunction;
		}

		public AccessorBuilder isPrivate() {
			this.isPrivate = true;
			return this;
		}

		public AccessorBuilder withAnnotation(Annotation annotation) {
			this.annotations.add(annotation);
			return this;
		}

		public AccessorBuilder withBody(KotlinExpressionStatement expressionStatement) {
			this.body = expressionStatement;
			return this;
		}

		public T buildAccessor() {
			this.accessorFunction.accept(new Accessor(this));
			return this.parent;
		}

	}

	static final class Accessor implements Annotatable {

		private final List<Annotation> annotations = new ArrayList<>();

		private final KotlinExpressionStatement body;

		private final boolean isPrivate;

		@SuppressWarnings("unchecked")
		Accessor(AccessorBuilder builder) {
			this.annotations.addAll(builder.annotations);
			this.body = builder.body;
			this.isPrivate = builder.isPrivate;
		}

		boolean isPrivate() {
			return this.isPrivate;
		}

		boolean isEmptyBody() {
			return this.body == null;
		}

		KotlinExpressionStatement getBody() {
			return this.body;
		}

		@Override
		public void annotate(Annotation annotation) {
			this.annotations.add(annotation);
		}

		@Override
		public List<Annotation> getAnnotations() {
			return Collections.unmodifiableList(this.annotations);
		}

	}

}
