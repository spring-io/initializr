/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller.custom;

import java.util.Objects;
import java.util.function.BiConsumer;

import io.spring.initializr.generator.project.ProjectDescriptionDiff;

/**
 * Extends the base {@link ProjectDescriptionDiff} to provide convenient diff methods on
 * {@link CustomProjectDescription}.
 *
 * @author Chris Bono
 * @author Stephane Nicoll
 */
class CustomProjectDescriptionDiff extends ProjectDescriptionDiff {

	private final CustomProjectDescription original;

	CustomProjectDescriptionDiff(CustomProjectDescription original) {
		super(original);
		this.original = original;
	}

	/**
	 * Calls the specified consumer if the {@code customFlag} is different on the original
	 * source project description than the specified project description.
	 * @param current the project description to test against
	 * @param consumer to call if the property has changed
	 */
	void ifCustomFlagChanged(CustomProjectDescription current, BiConsumer<Boolean, Boolean> consumer) {
		if (!Objects.equals(this.original.isCustomFlag(), current.isCustomFlag())) {
			consumer.accept(this.original.isCustomFlag(), current.isCustomFlag());
		}
	}

}
