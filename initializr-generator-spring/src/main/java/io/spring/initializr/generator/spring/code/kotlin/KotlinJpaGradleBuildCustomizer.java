/*
 * Copyright 2012-2023 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 *
 * {@link BuildCustomizer} for Gradle that configures the JPA Kotlin plugin if a JPA
 * related dependency is present.
 *
 * @author Madhura Bhave
 * @author Sijun Yang
 */
public class KotlinJpaGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final BuildMetadataResolver buildMetadataResolver;

	private final KotlinProjectSettings settings;

	private final char quote;

	public KotlinJpaGradleBuildCustomizer(InitializrMetadata metadata, KotlinProjectSettings settings,
			ProjectDescription projectDescription, char quote) {
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, projectDescription.getPlatformVersion());
		this.settings = settings;
		this.quote = quote;
	}

	@Override
	public void customize(GradleBuild build) {
		if (this.buildMetadataResolver.hasFacet(build, "jpa")) {
			build.plugins()
				.add("org.jetbrains.kotlin.plugin.jpa", (plugin) -> plugin.setVersion(this.settings.getVersion()));
			build.extensions().customize("allOpen", (allOpen) -> {
				allOpen.invoke("annotation", quote("jakarta.persistence.Entity"));
				allOpen.invoke("annotation", quote("jakarta.persistence.MappedSuperclass"));
				allOpen.invoke("annotation", quote("jakarta.persistence.Embeddable"));
			});
		}
	}

	private String quote(String element) {
		return this.quote + element + this.quote;
	}

}
