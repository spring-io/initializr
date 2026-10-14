/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.AnnotationContainer;
import io.spring.initializr.generator.language.CodeBlock;
import io.spring.initializr.generator.language.Parameter;

/**
 * Declaration of a method written in Java.
 *
 * @author Andy Wilkinson
 */
public final class JavaMethodDeclaration implements Annotatable {

	private final AnnotationContainer annotations = new AnnotationContainer();

	private final String name;

	private final String returnType;

	private final int modifiers;

	private final List<Parameter> parameters;

	private final CodeBlock code;

	private JavaMethodDeclaration(Builder builder, CodeBlock code) {
		this.name = builder.name;
		this.returnType = builder.returnType;
		this.modifiers = builder.modifiers;
		this.parameters = List.copyOf(builder.parameters);
		this.code = code;
	}

	/**
	 * Creates a new builder for the method with the given name.
	 * @param name the name
	 * @return the builder
	 */
	public static Builder method(String name) {
		return new Builder(name);
	}

	String getName() {
		return this.name;
	}

	String getReturnType() {
		return this.returnType;
	}

	List<Parameter> getParameters() {
		return this.parameters;
	}

	int getModifiers() {
		return this.modifiers;
	}

	CodeBlock getCode() {
		return this.code;
	}

	@Override
	public AnnotationContainer annotations() {
		return this.annotations;
	}

	/**
	 * Builder for creating a {@link JavaMethodDeclaration}.
	 */
	public static final class Builder {

		private final String name;

		private List<Parameter> parameters = new ArrayList<>();

		private String returnType = "void";

		private int modifiers;

		private Builder(String name) {
			this.name = name;
		}

		/**
		 * Sets the modifiers.
		 * @param modifiers the modifiers
		 * @return this for method chaining
		 */
		public Builder modifiers(int modifiers) {
			this.modifiers = modifiers;
			return this;
		}

		/**
		 * Sets the return type.
		 * @param returnType the return type
		 * @return this for method chaining
		 */
		public Builder returning(String returnType) {
			this.returnType = returnType;
			return this;
		}

		/**
		 * Sets the parameters.
		 * @param parameters the parameters
		 * @return this for method chaining
		 */
		public Builder parameters(Parameter... parameters) {
			this.parameters = Arrays.asList(parameters);
			return this;
		}

		/**
		 * Sets the body.
		 * @param code the code for the body
		 * @return the method containing the body
		 */
		public JavaMethodDeclaration body(CodeBlock code) {
			return new JavaMethodDeclaration(this, code);
		}

	}

}
