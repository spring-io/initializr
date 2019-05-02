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

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.BuildSystem;

/**
 * Gradle {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
public final class GradleBuildSystem implements BuildSystem {

	/**
	 * Gradle {@link BuildSystem} identifier.
	 */
	public static final String ID = "gradle";

	/**
	 * Gradle build using the Groovy DSL.
	 */
	public static final String DIALECT_GROOVY = "groovy";

	/**
	 * Gradle build using the Kotlin DSL.
	 */
	public static final String DIALECT_KOTLIN = "kotlin";

	private final String dialect;

	public GradleBuildSystem() {
		this(DIALECT_GROOVY);
	}

	public GradleBuildSystem(String dialect) {
		this.dialect = dialect;
	}

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String dialect() {
		return this.dialect;
	}

	@Override
	public String toString() {
		return id();
	}

}
