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

package io.spring.initializr.generator.spring.properties;

/**
 * A source set of a generated project, following the standard directory layout of the
 * supported build systems (for example {@code src/main} and {@code src/test}).
 *
 * @author Denis A. Altoé Falqueto
 */
public enum SourceSet {

	/**
	 * The main source set, packaged with the application.
	 */
	MAIN,

	/**
	 * The test source set, only available to tests.
	 */
	TEST

}
