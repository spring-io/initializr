/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

/**
 * Escapes values written as string literals in Gradle build scripts.
 *
 * @author Moritz Halbritter
 */
final class GradleDslEscaper {

	private GradleDslEscaper() {
	}

	/**
	 * Escape the specified value for a single-quoted Groovy string, e.g. {@code it's}
	 * becomes {@code it\'s}.
	 * @param value the value to escape
	 * @return the escaped value
	 */
	static String groovy(String value) {
		// Backslash first, so escapes added below are not doubled
		return value.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
	}

	/**
	 * Escape the specified value for a double-quoted Kotlin string, e.g. {@code a"${x}}
	 * becomes {@code a\"\${x}}.
	 * @param value the value to escape
	 * @return the escaped value
	 */
	static String kotlin(String value) {
		// Backslash first, so escapes added below are not doubled
		return value.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("$", "\\$")
			.replace("\n", "\\n")
			.replace("\r", "\\r");
	}

}
