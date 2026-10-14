/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

import java.nio.file.Path;

import io.spring.initializr.generator.test.io.TextTestUtils;

/**
 * Simple assertions for a kotlin build using the kotlin DSL.
 *
 * @author Prithvi singh
 * @author Stephane Nicoll
 */
public class KotlinDslGradleBuildAssert extends GradleBuildAssert<KotlinDslGradleBuildAssert> {

	public KotlinDslGradleBuildAssert(String content) {
		super(content, KotlinDslGradleBuildAssert.class);
	}

	public KotlinDslGradleBuildAssert(Path buildGradleFile) {
		this(TextTestUtils.readContent(buildGradleFile));
	}

	@Override
	protected String quote(String value) {
		return "\"" + value + "\"";
	}

	/**
	 * Assert {@code build.gradle.kts} defines a plugin with the specified id and version.
	 * @param id the id of the plugin
	 * @param version the version of the plugin
	 * @return {@code this} assertion object
	 */
	public KotlinDslGradleBuildAssert hasPlugin(String id, String version) {
		return contains("id(%s) version %s".formatted(quote(id), quote(version)));
	}

	/**
	 * Assert {@code build.gradle.kts} defines a plugin with the specified id.
	 * @param id the id of the plugin
	 * @return {@code this} assertion object
	 */
	public KotlinDslGradleBuildAssert hasPlugin(String id) {
		return contains("id(%s)".formatted(quote(id)));
	}

}
