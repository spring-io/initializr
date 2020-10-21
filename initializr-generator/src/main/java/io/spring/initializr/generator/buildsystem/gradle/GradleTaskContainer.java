/*
 * Copyright 2012-2020 the original author or authors.
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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.gradle.GradleTask.Builder;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * A container for {@linkplain GradleTask Gradle tasks}.
 *
 * @author Stephane Nicoll
 */
public class GradleTaskContainer {

	private final Map<String, Builder> tasks = new LinkedHashMap<>();

	private final Set<String> importedTypes = new HashSet<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no task is registered
	 */
	public boolean isEmpty() {
		return this.tasks.isEmpty();
	}

	/**
	 * Specify if this container has a task customization with the specified {@code name}.
	 * @param name the name of a task
	 * @return {@code true} if a customization for a task with the specified {@code name}
	 * exists
	 */
	public boolean has(String name) {
		return this.tasks.containsKey(name);
	}

	/**
	 * Return the {@link GradleTask Gradle tasks} to customize.
	 * @return the gradle tasks
	 */
	public Stream<GradleTask> values() {
		return this.tasks.values().stream().map(Builder::build);
	}

	/**
	 * Get a {@link GradleTask} with the specified task name.
	 * @param task the name or type
	 * @return the matching gradle task or {@code null}
	 */
	public GradleTask get(String task) {
		Builder builder = this.tasks.get(task);
		return (builder != null) ? builder.build() : null;
	}

	/**
	 * Return the fully qualified name of types to import.
	 * @return the imported types
	 */
	public Stream<String> importedTypes() {
		return this.importedTypes.stream();
	}

	/**
	 * Customize a task with the specified name. If the task has already been customized,
	 * the consumer can be used to further tune the existing task.
	 * @param name the name of a task
	 * @param task a callback to customize the task
	 */
	public void customize(String name, Consumer<Builder> task) {
		task.accept(this.tasks.computeIfAbsent(name, Builder::new));
	}

	/**
	 * Customize a task matching a given type. If the task has already been customized,
	 * the consumer can be used to further tune the existing task.
	 * @param type the name of type. Can use the short form for well-known types such as
	 * {@code JavaCompile}, use a fully qualified name if an import is required
	 * @param task a callback to customize the task
	 */
	public void customizeWithType(String type, Consumer<Builder> task) {
		String packageName = ClassUtils.getPackageName(type);
		if (StringUtils.hasLength(packageName)) {
			this.importedTypes.add(type);
		}
		String shortName = ClassUtils.getShortName(type);
		task.accept(this.tasks.computeIfAbsent(shortName, (name) -> new Builder(name, type)));
	}

	/**
	 * Remove the task with the specified {@code name}.
	 * @param name the name of the task
	 * @return {@code true} if such a task was registered, {@code false} otherwise
	 */
	public boolean remove(String name) {
		return this.tasks.remove(name) != null;
	}

}
