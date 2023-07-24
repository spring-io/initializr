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
		return contains(String.format("id(%s) version %s", quote(id), quote(version)));
	}

	/**
	 * Assert {@code build.gradle.kts} defines a plugin with the specified id.
	 * @param id the id of the plugin
	 * @return {@code this} assertion object
	 */
	public KotlinDslGradleBuildAssert hasPlugin(String id) {
		return contains(String.format("id(%s)", quote(id)));
	}

}
