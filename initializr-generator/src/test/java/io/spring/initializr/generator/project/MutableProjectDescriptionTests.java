/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import io.spring.initializr.generator.buildsystem.Dependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MutableProjectDescription}.
 *
 * @author Stephane Nicoll
 */
class MutableProjectDescriptionTests {

	@Test
	void removeDependencyWithExistingDependencyReturnsDependency() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		Dependency testDependency = mock(Dependency.class);
		description.addDependency("test", testDependency);
		assertThat(description.removeDependency("test")).isSameAs(testDependency);
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

	@Test
	void removeDependencyWithUnknownDependencyReturnsNull() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		assertThat(description.removeDependency("unknown")).isNull();
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

	@Test
	void createCopyCopiesChanges() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.getChanges().add(ProjectDescriptionField.JVM_VERSION, "test reason");
		MutableProjectDescription copy = description.createCopy();
		assertThat(copy.getChanges().getAll()).extracting(ProjectDescriptionChange::getReason)
			.containsExactly("test reason");
	}

	@Test
	void createCopyDoesNotShareChanges() {
		MutableProjectDescription description = new MutableProjectDescription();
		MutableProjectDescription copy = description.createCopy();
		description.getChanges().add(ProjectDescriptionField.JVM_VERSION, "test reason");
		assertThat(copy.getChanges().isEmpty()).isTrue();
	}

}
