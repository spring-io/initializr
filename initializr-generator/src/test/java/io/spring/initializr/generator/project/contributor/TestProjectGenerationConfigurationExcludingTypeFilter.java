/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import io.spring.initializr.generator.project.ProjectGenerationConfigurationTypeFilter;

public class TestProjectGenerationConfigurationExcludingTypeFilter implements ProjectGenerationConfigurationTypeFilter {

	private final ProjectGenerationConfigurationTypeFilter delegate = ProjectGenerationConfigurationTypeFilter
		.exclude(TestProjectGenerationConfiguration.class);

	@Override
	public boolean test(Class<?> type) {
		return this.delegate.test(type);
	}

}
