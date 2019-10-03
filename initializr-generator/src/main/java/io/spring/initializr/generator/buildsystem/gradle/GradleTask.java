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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A customization for a Gradle task.
 *
 * @author Stephane Nicoll
 */
public class GradleTask {

	private final String name;

	private final String type;

	private final Map<String, String> attributes;

	private final List<Invocation> invocations;

	private final Map<String, GradleTask> nested;

	protected GradleTask(Builder builder) {
		this.name = builder.name;
		this.type = builder.type;
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
		this.invocations = Collections.unmodifiableList(new ArrayList<>(builder.invocations));
		this.nested = Collections.unmodifiableMap(resolve(builder.nested));
	}

	private static Map<String, GradleTask> resolve(Map<String, Builder> tasks) {
		Map<String, GradleTask> result = new LinkedHashMap<>();
		tasks.forEach((name, builder) -> result.put(name, builder.build()));
		return result;
	}

	/**
	 * Return the name of the task.
	 * @return the task name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the type that this task is associated with or {@code null} if this task has
	 * no type.
	 * @return the associated task type or {@code null}
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Return the attributes that should be set for this task.
	 * @return task attributes
	 */
	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	/**
	 * Return the {@link Invocation invocations} of this task.
	 * @return task invocations
	 */
	public List<Invocation> getInvocations() {
		return this.invocations;
	}

	/**
	 * Return nested {@link GradleTask tasks}.
	 * @return nested tasks
	 */
	public Map<String, GradleTask> getNested() {
		return this.nested;
	}

	/**
	 * A builder for {@link GradleTask}.
	 */
	public static class Builder {

		private final String name;

		private final String type;

		private final Map<String, String> attributes = new LinkedHashMap<>();

		private final List<Invocation> invocations = new ArrayList<>();

		private final Map<String, Builder> nested = new LinkedHashMap<>();

		protected Builder(String name, String type) {
			this.name = name;
			this.type = type;
		}

		protected Builder(String name) {
			this(name, null);
		}

		/**
		 * Add a task attribute.
		 * @param target the name of the attribute
		 * @param value the value
		 */
		public void attribute(String target, String value) {
			this.attributes.put(target, value);
		}

		/**
		 * Invoke a task method.
		 * @param target the name of the method
		 * @param arguments the arguments
		 */
		public void invoke(String target, String... arguments) {
			this.invocations.add(new Invocation(target, Arrays.asList(arguments)));
		}

		/**
		 * Customize a nested task for the specified property. If such nested task has
		 * already been added, the consumer can be used to further tune the existing task
		 * configuration.
		 * @param property a task property
		 * @param customizer a {@link Consumer} to customize the nested task
		 */
		public void nested(String property, Consumer<Builder> customizer) {
			customizer.accept(this.nested.computeIfAbsent(property, (name) -> new Builder(property)));
		}

		/**
		 * Build a {@link GradleTask} with the current state of this builder.
		 * @return a {@link GradleTask}
		 */
		public GradleTask build() {
			return new GradleTask(this);
		}

	}

	/**
	 * An invocation of a method that customizes a task.
	 */
	public static class Invocation {

		private final String target;

		private final List<String> arguments;

		Invocation(String target, List<String> arguments) {
			this.target = target;
			this.arguments = arguments;
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

}
