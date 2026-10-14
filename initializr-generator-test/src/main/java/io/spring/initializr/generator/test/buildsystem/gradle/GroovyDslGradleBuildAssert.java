/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

import java.nio.file.Path;

import io.spring.initializr.generator.test.io.TextTestUtils;

/**
 * Simple assertions for a gradle build using the Groovy DSL.
 *
 * @author Stephane Nicoll
 */
public class GroovyDslGradleBuildAssert extends GradleBuildAssert<GroovyDslGradleBuildAssert> {

	public GroovyDslGradleBuildAssert(String content) {
		super(content, GroovyDslGradleBuildAssert.class);
	}

	public GroovyDslGradleBuildAssert(Path buildGradleFile) {
		this(TextTestUtils.readContent(buildGradleFile));
	}

	@Override
	protected String quote(String value) {
		return "'" + value + "'";
	}

	/**
	 * Assert {@code build.gradle} defines a plugin with the specified id and version.
	 * @param id the id of the plugin
	 * @param version the version of the plugin
	 * @return {@code this} assertion object
	 */
	public GroovyDslGradleBuildAssert hasPlugin(String id, String version) {
		return contains("id %s version %s".formatted(quote(id), quote(version)));
	}

	/**
	 * Assert {@code build.gradle} defines a plugin with the specified id.
	 * @param id the id of the plugin
	 * @return {@code this} assertion object
	 */
	public GroovyDslGradleBuildAssert hasPlugin(String id) {
		return contains("id %s".formatted(quote(id)));
	}

}
