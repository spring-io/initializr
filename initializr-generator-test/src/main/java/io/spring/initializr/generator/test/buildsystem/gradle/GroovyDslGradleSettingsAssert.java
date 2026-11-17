/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
