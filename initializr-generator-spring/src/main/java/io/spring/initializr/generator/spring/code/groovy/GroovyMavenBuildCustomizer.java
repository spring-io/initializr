/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionReference;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Maven.
 *
 * @author Stephane Nicoll
 */
class GroovyMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	@Override
	public void customize(MavenBuild build) {
		build.plugins().add("org.codehaus.gmavenplus", "gmavenplus-plugin", (groovyMavenPlugin) -> {
			groovyMavenPlugin.version(VersionReference.ofValue("1.11.0"));
			groovyMavenPlugin.execution(null,
					(execution) -> execution.goal("addSources").goal("addTestSources").goal("generateStubs")
							.goal("compile").goal("generateTestStubs").goal("compileTests").goal("removeStubs")
							.goal("removeTestStubs"));
		});
	}

}
