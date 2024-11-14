/*
 * Copyright 2012-2024 the original author or authors.
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
 * Allows to filter {@link ProjectGenerationConfiguration}.
 *
 * @author Simon Zambrovski
 */
public interface ProjectGenerationConfigurationTypeFilter {

	/**
	 * Determines if the provided class matches the filter.
	 * @param type type to match.
	 * @return true, if the type matches.
	 */
	boolean match(Class<?> type);

}
