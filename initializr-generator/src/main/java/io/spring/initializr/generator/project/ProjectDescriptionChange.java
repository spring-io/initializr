/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * A change that has been applied to a {@link ProjectDescription}, together with the
 * reason why it was applied.
 *
 * @author Moritz Halbritter
 * @see ProjectDescriptionChanges
 */
public class ProjectDescriptionChange {

	private final ProjectDescriptionField field;

	private final String reason;

	/**
	 * Creates a new instance.
	 * @param field the field that was changed
	 * @param reason a self-contained, human-readable explanation of why the field was
	 * changed
	 */
	public ProjectDescriptionChange(ProjectDescriptionField field, String reason) {
		this.field = field;
		this.reason = reason;
	}

	/**
	 * Return the field that was changed.
	 * @return the field
	 */
	public ProjectDescriptionField getField() {
		return this.field;
	}

	/**
	 * Return a self-contained, human-readable explanation of why the field was changed.
	 * @return the reason
	 */
	public String getReason() {
		return this.reason;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ProjectDescriptionChange other = (ProjectDescriptionChange) obj;
		return this.field == other.field && this.reason.equals(other.reason);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.field, this.reason);
	}

	@Override
	public String toString() {
		return this.field + ": " + this.reason;
	}

}
