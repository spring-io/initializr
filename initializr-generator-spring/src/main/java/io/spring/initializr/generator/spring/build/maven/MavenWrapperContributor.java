/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.project.contributor.MultipleResourcesProjectContributor;

/**
 * A {@link MultipleResourcesProjectContributor} that contributes Maven's wrapper to a
 * project.
 *
 * @author Andy Wilkinson
 */
class MavenWrapperContributor extends MultipleResourcesProjectContributor {

	MavenWrapperContributor(String mavenVersion) {
		super("classpath:maven/" + mavenVersion + "/wrapper",
				(filename) -> filename.equals("mvnw") || filename.equals("mvnw.cmd"));
	}

}
