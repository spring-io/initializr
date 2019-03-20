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

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnBuildSystem}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnBuildSystemTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(BuildSystemTestConfiguration.class);

	@Test
	void outcomeWithMavenBuildSystem() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setBuildSystem(new MavenBuildSystem());
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("testMaven");
	}

	@Test
	void outcomeWithGradleBuildSystem() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setBuildSystem(new GradleBuildSystem());
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("testGradle");
	}

	private String outcomeFor(ProjectDescription projectDescription) {
		return this.projectTester.generate(projectDescription,
				(projectGenerationContext) -> {
					assertThat(projectGenerationContext.getBeansOfType(String.class))
							.hasSize(1);
					return projectGenerationContext.getBean(String.class);
				});
	}

	@Configuration
	static class BuildSystemTestConfiguration {

		@Bean
		@ConditionalOnBuildSystem("gradle")
		public String gradle() {
			return "testGradle";
		}

		@Bean
		@ConditionalOnBuildSystem("maven")
		public String maven() {
			return "testMaven";
		}

	}

}
