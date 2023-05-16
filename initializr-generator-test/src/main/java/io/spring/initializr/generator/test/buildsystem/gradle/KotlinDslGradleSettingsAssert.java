/*
 * Copyright 2012-2019 the original author or authors.
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
 * Simple assertions for a gradle settings using the Kotlin DSL.
 *
 * @author Prithvi singh
 */
public class KotlinDslGradleSettingsAssert extends AbstractTextAssert<KotlinDslGradleSettingsAssert> {

	protected KotlinDslGradleSettingsAssert(String content) {
		super(content, KotlinDslGradleSettingsAssert.class);
	}

	/**
	 * Assert {@code settings.gradle.kts} defines the specified project name.
	 * @param name the name of the project
	 * @return {@code this} assertion object
	 */
	public KotlinDslGradleSettingsAssert hasProjectName(String name) {
		return hasProperty("rootProject.name", name);
	}

	/**
	 * Assert {@code settings.gradle.kts} defines a property with the specified name and
	 * value.
	 * @param name the name of the property
	 * @param value the value
	 * @return {@code this} assertion object
	 */
	public KotlinDslGradleSettingsAssert hasProperty(String name, String value) {
		return contains(String.format("%s = '%s", name, value));
	}

}
