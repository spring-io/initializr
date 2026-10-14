/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.buildsystem.gradle;

/**
 * Simple assertions for a gradle settings using the Groovy DSL.
 *
 * @author Stephane Nicoll
 */
public class GroovyDslGradleSettingsAssert extends GradleSettingsAssert<GroovyDslGradleSettingsAssert> {

	public GroovyDslGradleSettingsAssert(String content) {
		super(content, GroovyDslGradleSettingsAssert.class);
	}

}
