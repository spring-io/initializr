/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
