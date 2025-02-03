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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A type declared in a {@link CompilationUnit}.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
public class TypeDeclaration implements Annotatable {

	private final AnnotationContainer annotations = new AnnotationContainer();

	private final String name;

	private String extendedClassName;

	private List<String> implementsClassNames = Collections.emptyList();

	/**
	 * Creates a new instance.
	 * @param name the type name
	 */
	public TypeDeclaration(String name) {
		this.name = name;
	}

	/**
	 * Extend the class with the given name.
	 * @param name the name of the class to extend
	 */
	public void extend(String name) {
		this.extendedClassName = name;
	}

	/**
	 * Implement the given interfaces.
	 * @param names the names of the interfaces to implement
	 */
	public void implement(Collection<String> names) {
		this.implementsClassNames = List.copyOf(names);
	}

	/**
	 * Implement the given interfaces.
	 * @param names the names of the interfaces to implement
	 */
	public void implement(String... names) {
		this.implementsClassNames = List.of(names);
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

	public List<String> getImplements() {
		return this.implementsClassNames;
	}

}
