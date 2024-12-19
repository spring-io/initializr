/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.List;

/**
 * An invocation of a method.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
public class Invocation {

	private final String target;

	private final List<String> arguments;

	/**
	 * Creates a new instance.
	 * @param target the target
	 * @param arguments the arguments
	 */
	public Invocation(String target, List<String> arguments) {
		this.target = target;
		this.arguments = List.copyOf(arguments);
	}

	/**
	 * Return the name of the method.
	 * @return the method name
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * Return the arguments (can be empty).
	 * @return the method arguments
	 */
	public List<String> getArguments() {
		return this.arguments;
	}

}
