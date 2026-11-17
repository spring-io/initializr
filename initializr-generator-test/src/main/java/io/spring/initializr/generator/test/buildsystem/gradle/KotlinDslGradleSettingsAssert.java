/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
