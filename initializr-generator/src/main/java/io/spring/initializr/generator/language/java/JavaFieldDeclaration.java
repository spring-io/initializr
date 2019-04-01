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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.Annotation;

/**
 * Declaration, and potential initialization, of a field in Java.
 *
 * @author Matt Berteaux
 */
public final class JavaFieldDeclaration implements Annotatable {

	private final List<Annotation> annotations = new ArrayList<>();

	private final int modifiers;

	private final String name;

	private final String returnType;

	private final Object value;

	private JavaFieldDeclaration(int modifiers, String name, String returnType, Object value) {
		this.modifiers = modifiers;
		this.name = name;
		this.returnType = returnType;
		this.value = value;
	}

	public static Builder field(String name) {
		return new Builder(name);
	}

	String getName() {
		return this.name;
	}

	String getReturnType() {
		return this.returnType;
	}

	int getModifiers() {
		return this.modifiers;
	}

	Object getValue() {
		return this.value;
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
	 * Builder for creating a {@link JavaFieldDeclaration}.
	 */
	public static final class Builder {

		private final String name;

		private int modifiers = Modifier.PUBLIC;

		private Object value = InitializedStatus.NOT_INITIALIZED;

		private Builder(String name) {
			this.name = name;
		}

		public Builder packagePrivate() {
			this.modifiers = 0;
			return this;
		}

		public Builder modifiers(int modifiers) {
			this.modifiers = modifiers;
			return this;
		}

		public Builder value(JavaExpression value) {
			this.value = value;
			return this;
		}

		public JavaFieldDeclaration returning(String returnType) {
			return new JavaFieldDeclaration(this.modifiers, this.name, returnType, this.value);
		}

	}

	/**
	 * Track if the value has been set or not. Using this because initializing a field to
	 * null should be possible.
	 */
	enum InitializedStatus {

		NOT_INITIALIZED;

	}

}
