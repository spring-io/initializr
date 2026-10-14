/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

import io.spring.initializr.generator.test.io.AbstractTextAssert;

/**
 * Base class for Gradle settings assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public class GradleSettingsAssert<SELF extends GradleSettingsAssert<SELF>> extends AbstractTextAssert<SELF> {

	protected GradleSettingsAssert(String actual, Class<?> selfType) {
		super(actual, selfType);
	}

	/**
	 * Assert the Gradle {@code settings} defines the specified project name.
	 * @param name the name of the project
	 * @return {@code this} assertion object
	 */
	public SELF hasProjectName(String name) {
		return hasProperty("rootProject.name", name);
	}

	/**
	 * Assert the Gradle {@code settings} defines a property with the specified name and
	 * value.
	 * @param name the name of the property
	 * @param value the value
	 * @return {@code this} assertion object
	 */
	public SELF hasProperty(String name, String value) {
		return contains("%s = '%s".formatted(name, value));
	}

}
