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

package io.spring.initializr.generator.spring.test.build;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Very simple assertions for the gradle settings.
 *
 * @author Stephane Nicoll
 */
public class GradleSettingsAssert {

	private final String content;

	public GradleSettingsAssert(String content) {
		this.content = content;
	}

	/**
	 * Assert {@code settings.gradle} defines the specified project name.
	 * @param name the name of the project
	 * @return this
	 */
	public GradleSettingsAssert hasProjectName(String name) {
		return contains(String.format("rootProject.name = '%s'", name));
	}

	/**
	 * Assert {@code settings.gradle} contains the specified expression.
	 * @param expression an expected expression
	 * @return this
	 */
	public GradleSettingsAssert contains(String expression) {
		assertThat(this.content).contains(expression);
		return this;
	}

}
