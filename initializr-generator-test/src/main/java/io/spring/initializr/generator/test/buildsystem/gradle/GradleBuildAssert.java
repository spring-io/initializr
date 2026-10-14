/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

import io.spring.initializr.generator.test.io.AbstractTextAssert;

/**
 * Base class for Gradle build assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public abstract class GradleBuildAssert<SELF extends GradleBuildAssert<SELF>> extends AbstractTextAssert<SELF> {

	protected GradleBuildAssert(String content, Class<?> selfType) {
		super(content, selfType);
	}

	/**
	 * Assert the Gradle {@code build} uses the specified {@code version}.
	 * @param version the version of the build
	 * @return {@code this} assertion object
	 */
	public SELF hasVersion(String version) {
		return hasProperty("version", version);
	}

	/**
	 * Assert the Gradle {@code build} uses a source compatibility for the specified java
	 * version.
	 * @param javaVersion the java version
	 * @return {@code this} assertion object
	 */
	public SELF hasSourceCompatibility(String javaVersion) {
		return hasProperty("sourceCompatibility", javaVersion);
	}

	/**
	 * Assert the Gradle {@code build} uses a toolchain for the specified java version.
	 * @param javaVersion the java version
	 * @return {@code this} assertion object
	 */
	public SELF hasToolchainForJava(String javaVersion) {
		return containsSubsequence("java {", "toolchain {",
				"languageVersion = JavaLanguageVersion.of(%s)".formatted(javaVersion));
	}

	/**
	 * Assert the Gradle {@code build} defines a top-level property with the specified
	 * name and value.
	 * @param name the name of the property
	 * @param value the value
	 * @return {@code this} assertion object
	 */
	public SELF hasProperty(String name, String value) {
		return contains("%s = %s".formatted(name, quote(value)));
	}

	/**
	 * Assert the Gradle {@code build} contains only the specified properties.
	 * @param values the property value pairs
	 * @return {@code this} assertion object
	 */
	public SELF containsOnlyExtProperties(String... values) {
		StringBuilder builder = new StringBuilder("ext {%n".formatted());
		if (values.length % 2 == 1) {
			throw new IllegalArgumentException("Size must be even, it is a set of property=value pairs");
		}
		for (int i = 0; i < values.length; i += 2) {
			builder.append("\tset(%s, \"%s\")%n".formatted(quote(values[i]), values[i + 1]));
		}
		builder.append("}");
		return contains(builder.toString());
	}

	/**
	 * Quote the given value according to the Gradle DSL.
	 * @param value the value
	 * @return the quoted value
	 */
	protected abstract String quote(String value);

}
