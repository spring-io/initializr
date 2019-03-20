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

package io.spring.initializr.generator.buildsystem;

/**
 * The scopes of dependencies supported by project generation.
 *
 * @author Andy Wilkinson
 */
public enum DependencyScope {

	/**
	 * A dependency that is used as an annotation processor when compiling a project.
	 */
	ANNOTATION_PROCESSOR,

	/**
	 * A dependency that is used to compile a project.
	 */
	COMPILE,

	/**
	 * A dependency that is a compile time only dependency and not used at runtime.
	 */
	COMPILE_ONLY,

	/**
	 * A dependency this is used to run a project.
	 */
	RUNTIME,

	/**
	 * A dependency that is provided and is used to run the project.
	 */
	PROVIDED_RUNTIME,

	/**
	 * A dependency that is used to compile a project's tests.
	 */
	TEST_COMPILE,

	/**
	 * A dependency this is used to run a project's tests.
	 */
	TEST_RUNTIME

}
