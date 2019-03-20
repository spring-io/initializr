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

package io.spring.initializr.generator.project;

import java.io.IOException;

/**
 * Generate project assets using a {@link ProjectGenerationContext}.
 *
 * @param <T> the type that gathers the project assets
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectAssetGenerator<T> {

	/**
	 * Generate project assets using the specified {@link ProjectGenerationContext}.
	 * @param context the context to use
	 * @return the type that gathers the project assets
	 * @throws IOException if writing project assets failed
	 */
	T generate(ProjectGenerationContext context) throws IOException;

}
