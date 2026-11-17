/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
