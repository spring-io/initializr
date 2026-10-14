/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link ProjectDescriptionChanges}.
 *
 * @author Moritz Halbritter
 */
class ProjectDescriptionChangesTests {

	private final ProjectDescriptionChanges changes = new ProjectDescriptionChanges();

	@Test
	void emptyByDefault() {
		assertThat(this.changes.isEmpty()).isTrue();
		assertThat(this.changes.getAll()).isEmpty();
		assertThat(this.changes.get(ProjectDescriptionField.JVM_VERSION)).isNull();
	}

	@Test
	void addRecordsChange() {
		this.changes.add(ProjectDescriptionField.JVM_VERSION, "test reason");
		assertThat(this.changes.isEmpty()).isFalse();
		assertThat(this.changes.getAll())
			.containsExactly(new ProjectDescriptionChange(ProjectDescriptionField.JVM_VERSION, "test reason"));
	}

	@Test
	void addKeepsOnlyTheLastReasonOfAField() {
		this.changes.add(ProjectDescriptionField.JVM_VERSION, "first");
		this.changes.add(ProjectDescriptionField.JVM_VERSION, "second");
		assertThat(this.changes.getAll()).extracting(ProjectDescriptionChange::getReason).containsExactly("second");
		assertThat(this.changes.get(ProjectDescriptionField.JVM_VERSION)).isNotNull()
			.extracting(ProjectDescriptionChange::getReason)
			.isEqualTo("second");
	}

	@Test
	void getAllReturnsUnmodifiableList() {
		this.changes.add(ProjectDescriptionField.JVM_VERSION, "test reason");
		assertThat(this.changes.getAll()).isUnmodifiable();
	}

	@Test
	void addWithEmptyReasonIsRejected() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.changes.add(ProjectDescriptionField.JVM_VERSION, ""))
			.withMessage("'reason' must not be empty");
	}

}
