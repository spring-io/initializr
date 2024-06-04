/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		return contains(String.format("%s = %s", name, quote(value)));
	}

	/**
	 * Assert the Gradle {@code build} contains only the specified properties.
	 * @param values the property value pairs
	 * @return {@code this} assertion object
	 */
	public SELF containsOnlyExtProperties(String... values) {
		StringBuilder builder = new StringBuilder(String.format("ext {%n"));
		if (values.length % 2 == 1) {
			throw new IllegalArgumentException("Size must be even, it is a set of property=value pairs");
		}
		for (int i = 0; i < values.length; i += 2) {
			builder.append(String.format("\tset(%s, \"%s\")%n", quote(values[i]), values[i + 1]));
		}
		builder.append("}");
		return contains(builder.toString());
	}

	protected abstract String quote(String value);

}
