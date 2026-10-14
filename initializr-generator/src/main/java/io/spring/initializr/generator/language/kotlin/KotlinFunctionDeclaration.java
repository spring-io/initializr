/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.AnnotationContainer;
import io.spring.initializr.generator.language.CodeBlock;
import io.spring.initializr.generator.language.Parameter;
import org.jspecify.annotations.Nullable;

/**
 * Declaration of a function written in Kotlin.
 *
 * @author Stephane Nicoll
 */
public final class KotlinFunctionDeclaration implements Annotatable {

	private final AnnotationContainer annotations = new AnnotationContainer();

	private final String name;

	private final @Nullable String returnType;

	private final List<KotlinModifier> modifiers;

	private final List<Parameter> parameters;

	private final CodeBlock code;

	private KotlinFunctionDeclaration(Builder builder, CodeBlock code) {
		this.name = builder.name;
		this.returnType = builder.returnType;
		this.modifiers = builder.modifiers;
		this.parameters = List.copyOf(builder.parameters);
		this.code = code;
	}

	/**
	 * Creates a new builder for the function with the given name.
	 * @param name the name
	 * @return the builder
	 */
	public static Builder function(String name) {
		return new Builder(name);
	}

	String getName() {
		return this.name;
	}

	@Nullable String getReturnType() {
		return this.returnType;
	}

	List<Parameter> getParameters() {
		return this.parameters;
	}

	List<KotlinModifier> getModifiers() {
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
	 * Builder for creating a {@link KotlinFunctionDeclaration}.
	 */
	public static final class Builder {

		private final String name;

		private List<Parameter> parameters = new ArrayList<>();

		private List<KotlinModifier> modifiers = new ArrayList<>();

		private @Nullable String returnType;

		private Builder(String name) {
			this.name = name;
		}

		/**
		 * Sets the modifiers.
		 * @param modifiers the modifiers
		 * @return this for method chaining
		 */
		public Builder modifiers(KotlinModifier... modifiers) {
			this.modifiers = Arrays.asList(modifiers);
			return this;
		}

		/**
		 * Sets the return type.
		 * @param returnType the return type
		 * @return this for method chaining
		 */
		public Builder returning(@Nullable String returnType) {
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
		 * @return the function declaration containing the body
		 */
		public KotlinFunctionDeclaration body(CodeBlock code) {
			return new KotlinFunctionDeclaration(this, code);
		}

	}

}
