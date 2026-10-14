/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * Records the changes that have been applied to a {@link ProjectDescription} and the
 * reason why they were applied.
 * <p>
 * A {@link ProjectDescriptionCustomizer} that modifies a field should record a reason for
 * doing so, so that the reason can be reported to the user rather than inferred from a
 * {@link ProjectDescriptionDiff}. A reason must only be recorded if the field is actually
 * changed, and should be a self-contained sentence as it is reported as-is. Any markup
 * that a reason uses has to match what the component reporting it expects, see the
 * reference documentation.
 * <p>
 * At most one reason is kept per field: if several customizers change the same field,
 * only the reason of the last one is kept as it supersedes the previous ones.
 * Consequently, a reason should describe the value that has been set and why, but not the
 * value that has been replaced, as the latter may well be an intermediate value that no
 * other component has seen.
 * <p>
 * Each {@link MutableProjectDescription} has its own instance, see
 * {@link MutableProjectDescription#getChanges()}. Customizers are typically shared by
 * concurrent project generations, so they must record against the description they are
 * given rather than hold on to an instance of this class.
 *
 * @author Moritz Halbritter
 */
public class ProjectDescriptionChanges {

	private final Map<ProjectDescriptionField, ProjectDescriptionChange> changes = new LinkedHashMap<>();

	/**
	 * Record the reason why the given field has been changed, replacing any reason that
	 * has already been recorded for that field.
	 * @param field the field that has been changed
	 * @param reason a self-contained, human-readable explanation of why the field has
	 * been changed
	 */
	public void add(ProjectDescriptionField field, String reason) {
		Assert.hasText(reason, "'reason' must not be empty");
		this.changes.put(field, new ProjectDescriptionChange(field, reason));
	}

	/**
	 * Add all the changes that have been recorded in the given {@code source}.
	 * @param source the changes to add
	 */
	void addAll(ProjectDescriptionChanges source) {
		this.changes.putAll(source.changes);
	}

	/**
	 * Return the change that has been recorded for the given field.
	 * @param field the field to consider
	 * @return the change recorded for the given field, or {@code null} if there is none
	 */
	public @Nullable ProjectDescriptionChange get(ProjectDescriptionField field) {
		return this.changes.get(field);
	}

	/**
	 * Return all recorded changes, in the order in which their field has been changed for
	 * the first time.
	 * @return all recorded changes
	 */
	public List<ProjectDescriptionChange> getAll() {
		return Collections.unmodifiableList(new ArrayList<>(this.changes.values()));
	}

	/**
	 * Return whether no change has been recorded.
	 * @return {@code true} if no change has been recorded
	 */
	public boolean isEmpty() {
		return this.changes.isEmpty();
	}

}
