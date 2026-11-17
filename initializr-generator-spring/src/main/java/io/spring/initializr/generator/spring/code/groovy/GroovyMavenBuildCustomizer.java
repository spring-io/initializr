/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Maven.
 *
 * @author Stephane Nicoll
 */
class GroovyMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	@Override
	public void customize(MavenBuild build) {
		build.plugins().add("org.codehaus.gmavenplus", "gmavenplus-plugin", (groovyMavenPlugin) -> {
			groovyMavenPlugin.version("1.13.1");
			groovyMavenPlugin.execution(null,
					(execution) -> execution.goal("addSources")
						.goal("addTestSources")
						.goal("generateStubs")
						.goal("compile")
						.goal("generateTestStubs")
						.goal("compileTests")
						.goal("removeStubs")
						.goal("removeTestStubs"));
		});
	}

}
