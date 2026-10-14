/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleTask.Attribute;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleTaskContainer}.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("removal")
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
		container.customize("test", (task) -> {
			task.attribute("fork", "true");
			task.append("names", "test");
		});
		assertThat(container.values()).singleElement().satisfies((task) -> {
			assertThat(task.getName()).isEqualTo("test");
			assertThat(task.getType()).isNull();
			assertThat(task.getAttributes()).containsOnly(Attribute.set("fork", "true"),
					Attribute.append("names", "test"));
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
			assertThat(task.getAttributes()).containsOnly(Attribute.set("fork", "true"));
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
			assertThat(task.getAttributes()).containsOnly(Attribute.set("ignore", "false"),
					Attribute.set("fork", "false"));
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
