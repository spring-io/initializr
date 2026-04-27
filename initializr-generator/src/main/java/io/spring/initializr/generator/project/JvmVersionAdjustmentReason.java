/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.project;

/**
 * Why a project {@link ProjectDescription} had its JVM level adjusted during
 * customization.
 *
 * @author Moritz Halbritter
 */
public enum JvmVersionAdjustmentReason {

	/**
	 * The selected Spring Boot / platform version requires a higher JVM level.
	 */
	SPRING_BOOT,

	/**
	 * The selected Kotlin version does not support the requested JVM level.
	 */
	KOTLIN_COMPILER,

	/**
	 * A selected dependency requires a higher JVM level than was in effect.
	 */
	SELECTED_DEPENDENCY

}
