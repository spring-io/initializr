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

/**
 * A type declared in a {@link CompilationUnit}.
 *
 * @author Andy Wilkinson
 */
public class TypeDeclaration implements Annotatable {

	private final AnnotationContainer annotations = new AnnotationContainer();

	private final String name;

	private String extendedClassName;

	public TypeDeclaration(String name) {
		this.name = name;
	}

	public void extend(String name) {
		this.extendedClassName = name;
	}

	@Override
	public AnnotationContainer annotations() {
		return this.annotations;
	}

	public String getName() {
		return this.name;
	}

	public String getExtends() {
		return this.extendedClassName;
	}

}
