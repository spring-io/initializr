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
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.language.TypeDeclaration;

/**
 * A {@link TypeDeclaration declaration } of a type written in Kotlin.
 *
 * @author Stephane Nicoll
 */
public class KotlinTypeDeclaration extends TypeDeclaration {

	private List<KotlinModifier> modifiers = new ArrayList<>();

	private final List<KotlinFunctionDeclaration> functionDeclarations = new ArrayList<>();

	KotlinTypeDeclaration(String name) {
		super(name);
	}

	public void modifiers(KotlinModifier... modifiers) {
		this.modifiers = Arrays.asList(modifiers);
	}

	List<KotlinModifier> getModifiers() {
		return this.modifiers;
	}

	public void addFunctionDeclaration(KotlinFunctionDeclaration methodDeclaration) {
		this.functionDeclarations.add(methodDeclaration);
	}

	public List<KotlinFunctionDeclaration> getFunctionDeclarations() {
		return this.functionDeclarations;
	}

}
