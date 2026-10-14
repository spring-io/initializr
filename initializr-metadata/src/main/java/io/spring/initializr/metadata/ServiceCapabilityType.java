/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

/**
 * Defines the supported service capability type.
 *
 * @author Stephane Nicoll
 */
public enum ServiceCapabilityType {

	/**
	 * A special type that defines the action to use.
	 */
	ACTION("action"),

	/**
	 * A simple text value with no option.
	 */
	TEXT("text"),

	/**
	 * A simple value to be chosen amongst the specified options.
	 */
	SINGLE_SELECT("single-select"),

	/**
	 * A hierarchical set of values (values in values) with the ability to select multiple
	 * values.
	 */
	HIERARCHICAL_MULTI_SELECT("hierarchical-multi-select");

	private final String name;

	ServiceCapabilityType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
