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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link GradleTaskContainer}.
 *
 * @author Stephane Nicoll
 */
class GradleTaskContainerTests {

	@Test
	void isEmptyWithEmptyContainer() {
		GradleTaskContainer container = new GradleTaskContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithTask() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void isEmptyWithTaskWithType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("JavaCompile", (task) -> task.attribute("fork", "true"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingTask() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		assertThat(container.has("test")).isTrue();
	}

	@Test
	void hasWithMatchingTaskWithType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("JavaCompile", (task) -> task.attribute("fork", "true"));
		assertThat(container.has("JavaCompile")).isTrue();
	}

	@Test
	void hasWithNonMatchingNameOrType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		container.customizeWithType("JavaCompile", (task) -> task.attribute("fork", "true"));
		assertThat(container.has("another")).isFalse();
	}

	@Test
	void customizeTask() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		assertThat(container.values()).singleElement().satisfies((task) -> {
			assertThat(task.getName()).isEqualTo("test");
			assertThat(task.getType()).isNull();
			assertThat(task.getAttributes()).containsOnly(entry("fork", "true"));
			assertThat(task.getInvocations()).isEmpty();
			assertThat(task.getNested()).isEmpty();
		});
	}

	@Test
	void customizeTaskWithType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("com.example.MyTask", (task) -> {
			task.attribute("fork", "true");
			task.invoke("property", "taskDir");
		});
		assertThat(container.values()).singleElement().satisfies((task) -> {
			assertThat(task.getName()).isEqualTo("MyTask");
			assertThat(task.getType()).isEqualTo("com.example.MyTask");
			assertThat(task.getAttributes()).containsOnly(entry("fork", "true"));
			assertThat(task.getInvocations()).singleElement().satisfies((invocation) -> {
				assertThat(invocation.getTarget()).isEqualTo("property");
				assertThat(invocation.getArguments()).containsOnly("taskDir");
			});
			assertThat(task.getNested()).isEmpty();
		});
	}

	@Test
	void customizeTaskSeveralTimeReuseConfiguration() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> {
			task.attribute("ignore", "false");
			task.attribute("fork", "true");
		});
		container.customize("test", (task) -> {
			task.attribute("fork", "false");
			task.invoke("method", "arg1", "arg2");
		});
		assertThat(container.values()).singleElement().satisfies((task) -> {
			assertThat(task.getName()).isEqualTo("test");
			assertThat(task.getType()).isNull();
			assertThat(task.getAttributes()).containsOnly(entry("ignore", "false"), entry("fork", "false"));
			assertThat(task.getInvocations()).singleElement().satisfies((invocation) -> {
				assertThat(invocation.getTarget()).isEqualTo("method");
				assertThat(invocation.getArguments()).containsOnly("arg1", "arg2");
			});
			assertThat(task.getNested()).isEmpty();
		});
	}

	@Test
	void customizeTaskWithFqnImportType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("com.example.MyTask", (task) -> {
		});
		assertThat(container.importedTypes()).containsOnly("com.example.MyTask");
	}

	@Test
	void customizeTaskWithWellKnownTypeDoesNotImportType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("JavaCompile", (task) -> {
		});
		assertThat(container.importedTypes()).isEmpty();
	}

	@Test
	void removeWithMatchingTask() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		assertThat(container.remove("test")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithMatchingTaskWithType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customizeWithType("JavaCompile", (task) -> task.attribute("fork", "true"));
		assertThat(container.remove("JavaCompile")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingNameOrType() {
		GradleTaskContainer container = new GradleTaskContainer();
		container.customize("test", (task) -> task.attribute("fork", "true"));
		container.customizeWithType("JavaCompile", (task) -> task.attribute("fork", "true"));
		assertThat(container.remove("another")).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

}
