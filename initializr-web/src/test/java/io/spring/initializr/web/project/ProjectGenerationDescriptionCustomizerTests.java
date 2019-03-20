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

package io.spring.initializr.web.project;

import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.project.ProjectGenerationDescriptionCustomizerTests.ProjectDescriptionCustomizerConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-default")
@Import(ProjectDescriptionCustomizerConfiguration.class)
class ProjectGenerationDescriptionCustomizerTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	void projectDescriptionCustomizersAreInvoked() {
		downloadZip("/starter.zip?bootVersion=2.0.4.RELEASE&javaVersion=1.8")
				.isJavaProject().isMavenProject().pomAssert()
				.hasSpringBootParent("2.2.3.RELEASE").hasProperty("java.version", "1.7");
	}

	@Configuration
	static class ProjectDescriptionCustomizerConfiguration {

		@Bean
		public ProjectDescriptionCustomizer secondPostProcessor() {
			return new ProjectDescriptionCustomizer() {
				@Override
				public void customize(ProjectDescription description) {
					description.setLanguage(new JavaLanguage("1.7"));
				}

				@Override
				public int getOrder() {
					return 2;
				}
			};
		}

		@Bean
		public ProjectDescriptionCustomizer firstPostProcessor() {
			return new ProjectDescriptionCustomizer() {
				@Override
				public void customize(ProjectDescription description) {
					description.setLanguage(new JavaLanguage("1.2"));
					description.setPlatformVersion(Version.parse("2.2.3.RELEASE"));
				}

				@Override
				public int getOrder() {
					return 1;
				}
			};
		}

	}

}
