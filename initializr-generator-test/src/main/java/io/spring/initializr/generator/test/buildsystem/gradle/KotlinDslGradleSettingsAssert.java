/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

/**
 * Simple assertions for a gradle settings using the Kotlin DSL.
 *
 * @author Prithvi singh
 */
public class KotlinDslGradleSettingsAssert extends GradleSettingsAssert<KotlinDslGradleSettingsAssert> {

	protected KotlinDslGradleSettingsAssert(String content) {
		super(content, KotlinDslGradleSettingsAssert.class);
	}

}
