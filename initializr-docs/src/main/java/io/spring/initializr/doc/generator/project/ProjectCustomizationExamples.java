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

package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.generator.project.ProjectDescriptionField;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.spring.properties.SourceSet;

import org.springframework.context.annotation.Bean;

/**
 * Examples of customizers.
 *
 * @author Stephane Nicoll
 */
public class ProjectCustomizationExamples {

	// tag::war-plugin-contributor[]
	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	@ConditionalOnPackaging(WarPackaging.ID)
	public BuildCustomizer<GradleBuild> warPluginContributor() {
		return (build) -> build.plugins().add("war");
	}
	// end::war-plugin-contributor[]

	// tag::jvm-version-change-reason[]
	@Bean
	public ProjectDescriptionCustomizer jvmVersionProjectDescriptionCustomizer() {
		return (description) -> {
			Language language = description.getLanguage();
			if (language == null || !isBelowJava17(language.jvmVersion())) {
				return;
			}
			description.setLanguage(Language.forId(language.id(), "17"));
			description.getChanges()
				.add(ProjectDescriptionField.JVM_VERSION,
						"The JVM level was changed to '17' as it is the oldest supported version.");
		};
	}

	private boolean isBelowJava17(String jvmVersion) {
		try {
			return Integer.parseInt(jvmVersion) < 17;
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}
	// end::jvm-version-change-reason[]

	// tag::application-properties-customizer[]
	@Bean
	public ApplicationPropertiesCustomizer applicationPropertiesCustomizer() {
		return (properties) -> {
			// src/main/resources/application.properties (or .yaml)
			properties.add("spring.application.name", "acme");
			// src/test/resources/application.properties
			properties.section(SourceSet.TEST).add("spring.datasource.url", "jdbc:h2:mem:acme");
			// src/main/resources/application-dev.properties
			properties.section(SourceSet.MAIN, "dev").add("logging.level.root", "DEBUG");
		};
	}
	// end::application-properties-customizer[]

}
