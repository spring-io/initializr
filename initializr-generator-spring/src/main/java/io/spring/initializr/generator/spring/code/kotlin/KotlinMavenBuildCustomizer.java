/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Maven when Kotlin is supported
 * by the parent and only a partial configuration is required. Consider using
 * {@link KotlinMavenFullBuildCustomizer} for cases where the parent does not provide a
 * base configuration for Kotlin.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class KotlinMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final KotlinProjectSettings settings;

	KotlinMavenBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
		this.settings = kotlinProjectSettings;
	}

	@Override
	public void customize(MavenBuild build) {
		build.setProperty("kotlin.version", this.settings.getVersion());
		build.setSourceDirectory("${project.basedir}/src/main/kotlin");
		build.setTestSourceDirectory("${project.basedir}/src/test/kotlin");
		MavenPlugin kotlinMavenPlugin = build.plugin("org.jetbrains.kotlin",
				"kotlin-maven-plugin");
		kotlinMavenPlugin.configuration((configuration) -> {
			configuration.configure("args", (args) -> this.settings.getCompilerArgs()
					.forEach((arg) -> args.add("arg", arg)));
			configuration.configure("compilerPlugins",
					(compilerPlugins) -> compilerPlugins.add("plugin", "spring"));
		});
		kotlinMavenPlugin.dependency("org.jetbrains.kotlin", "kotlin-maven-allopen",
				"${kotlin.version}");
	}

}
