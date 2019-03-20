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

import java.util.Arrays;
import java.util.List;

/**
 * Invocation of a function with a reified type parameter.
 *
 * @author Stephane Nicoll
 */
public class KotlinReifiedFunctionInvocation extends KotlinExpression {

	private final String name;

	private final String targetClass;

	private final List<String> arguments;

	public KotlinReifiedFunctionInvocation(String name, String targetClass,
			String... arguments) {
		this.name = name;
		this.targetClass = targetClass;
		this.arguments = Arrays.asList(arguments);
	}

	public String getName() {
		return this.name;
	}

	public String getTargetClass() {
		return this.targetClass;
	}

	public List<String> getArguments() {
		return this.arguments;
	}

}
