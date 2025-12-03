/*
 * Copyright 2012 - present the original author or authors.
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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.ClassUtils;

/**
 * A {@link BuildCustomizer} that automatically adds "jackson-module-kotlin" when Kotlin
 * is used and a dependency has the "json" facet.
 *
 * @author Sebastien Deleuze
 * @author Madhura Bhave
 */
public class KotlinJacksonBuildCustomizer implements BuildCustomizer<Build> {

	private final BuildMetadataResolver buildMetadataResolver;

	private final ProjectDescription description;

	private static final VersionRange SPRING_BOOT_4_OR_LATER = VersionParser.DEFAULT.parseRange("4.0.0");

	public KotlinJacksonBuildCustomizer(InitializrMetadata metadata, ProjectDescription description) {
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, description.getPlatformVersion());
		this.description = description;
	}

	@Override
	public void customize(Build build) {
		boolean isKotlin = ClassUtils.isAssignableValue(KotlinLanguage.class, this.description.getLanguage());
		if (this.buildMetadataResolver.hasFacet(build, "json") && isKotlin) {
			String groupId = SPRING_BOOT_4_OR_LATER.match(this.description.getPlatformVersion())
					? "tools.jackson.module" : "com.fasterxml.jackson.module";
			build.dependencies()
				.add("jackson-module-kotlin", groupId, "jackson-module-kotlin", DependencyScope.COMPILE);
		}
	}

}
