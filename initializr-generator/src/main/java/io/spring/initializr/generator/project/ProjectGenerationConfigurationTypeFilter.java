/*
 * Copyright 2012 - present the original author or authors.
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
