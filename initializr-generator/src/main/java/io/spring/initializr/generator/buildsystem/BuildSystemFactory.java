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
 * A factory for creating a {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
public interface BuildSystemFactory {

	/**
	 * Creates and returns a {@link BuildSystem} for the given id. If the factory does not
	 * recognise the given {@code id}, {@code null} should be returned.
	 * @param id the id of the build system
	 * @return the build system or {@code null}
	 */
	BuildSystem createBuildSystem(String id);

	/**
	 * Creates and returns a {@link BuildSystem} for the given {@code id} and
	 * {@code dialect}. If the factory does not recognize the given {@code id} or
	 * {@code dialect}, {@code null} should be returned.
	 * @param id the id of the build system
	 * @param dialect the dialect of the build system
	 * @return the build system or {@code null}
	 */
	default BuildSystem createBuildSystem(String id, String dialect) {
		return createBuildSystem(id);
	}

}
