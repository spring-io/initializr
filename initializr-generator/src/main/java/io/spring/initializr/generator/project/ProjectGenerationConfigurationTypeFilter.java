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

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Allows to filter {@link ProjectGenerationConfiguration}.
 *
 * @author Simon Zambrovski
 * @author Moritz Halbritter
 */
public interface ProjectGenerationConfigurationTypeFilter extends Predicate<Class<?>> {

	/**
	 * Creates a {@link ProjectGenerationConfigurationTypeFilter} which includes the given
	 * types.
	 * @param types the types to include
	 * @return a {@link ProjectGenerationConfigurationTypeFilter}
	 */
	static ProjectGenerationConfigurationTypeFilter include(Class<?>... types) {
		Set<Class<?>> classes = Set.of(types);
		return classes::contains;
	}

	/**
	 * Creates a {@link ProjectGenerationConfigurationTypeFilter} which excludes the given
	 * types.
	 * @param types the types to exclude
	 * @return a {@link ProjectGenerationConfigurationTypeFilter}
	 */
	static ProjectGenerationConfigurationTypeFilter exclude(Class<?>... types) {
		Set<Class<?>> classes = Set.of(types);
		return (clazz) -> !classes.contains(clazz);
	}

	/**
	 * Creates a {@link ProjectGenerationConfigurationTypeFilter} from multiple filters
	 * which must all match.
	 * @param filters the filters
	 * @return a combined {@link ProjectGenerationConfigurationTypeFilter}
	 */
	static ProjectGenerationConfigurationTypeFilter allMatch(ProjectGenerationConfigurationTypeFilter... filters) {
		return allMatch(Arrays.asList(filters));
	}

	/**
	 * Creates a {@link ProjectGenerationConfigurationTypeFilter} from multiple filters
	 * which must all match.
	 * @param filters the filters
	 * @return a combined {@link ProjectGenerationConfigurationTypeFilter}
	 */
	static ProjectGenerationConfigurationTypeFilter allMatch(
			Iterable<? extends ProjectGenerationConfigurationTypeFilter> filters) {
		return (clazz) -> {
			boolean match = true;
			for (ProjectGenerationConfigurationTypeFilter filter : filters) {
				match &= filter.test(clazz);
			}
			return match;
		};
	}

}
