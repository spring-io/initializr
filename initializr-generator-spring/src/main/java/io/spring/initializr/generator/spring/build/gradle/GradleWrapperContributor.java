/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.project.contributor.MultipleResourcesProjectContributor;

/**
 * A {@link MultipleResourcesProjectContributor} that contributes Gradle's wrapper to a
 * project.
 *
 * @author Andy Wilkinson
 */
class GradleWrapperContributor extends MultipleResourcesProjectContributor {

	GradleWrapperContributor(String gradleVersion) {
		super("classpath:gradle/" + gradleVersion + "/wrapper",
				(filename) -> filename.equals("gradlew") || filename.equals("gradlew.bat"));
	}

}
