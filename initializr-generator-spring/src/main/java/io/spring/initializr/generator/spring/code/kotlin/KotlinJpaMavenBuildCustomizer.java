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
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A {@link BuildCustomizer} for Maven that configures the JPA Kotlin plugin if a JPA
 * related dependency is present.
 *
 * @author Madhura Bhave
 * @author Sebastien Deleuze
 * @author Sijun Yang
 */
public class KotlinJpaMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final BuildMetadataResolver buildMetadataResolver;

	public KotlinJpaMavenBuildCustomizer(InitializrMetadata metadata, ProjectDescription projectDescription) {
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, projectDescription.getPlatformVersion());
	}

	@Override
	public void customize(MavenBuild build) {
		if (this.buildMetadataResolver.hasFacet(build, "jpa")) {
			build.plugins().add("org.jetbrains.kotlin", "kotlin-maven-plugin", (kotlinPlugin) -> {
				kotlinPlugin.configuration((configuration) -> {
					configuration.configure("compilerPlugins",
							(compilerPlugins) -> compilerPlugins.add("plugin", "jpa"));
					configuration.configure("pluginOptions", (option) -> {
						option.add("option", "all-open:annotation=jakarta.persistence.Entity");
						option.add("option", "all-open:annotation=jakarta.persistence.MappedSuperclass");
						option.add("option", "all-open:annotation=jakarta.persistence.Embeddable");
					});
				});
				kotlinPlugin.dependency("org.jetbrains.kotlin", "kotlin-maven-noarg", "${kotlin.version}");
			});
		}
	}

}
