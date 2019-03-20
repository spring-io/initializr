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

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnPlatformVersion}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnPlatformVersionTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(PlatformVersionTestConfiguration.class);

	@Test
	void outcomeWithMatchingRange() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("1.2.0.RELEASE"));
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("one");
	}

	@Test
	void outcomeWithMatchingOpenRange() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.1.RELEASE"));
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("two");
	}

	@Test
	void outcomeWithMatchingStartOfOpenRange() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.0.M1"));
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("two");
	}

	@Test
	void outcomeWithNoMatch() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("0.1.0"));
		this.projectTester.generate(projectDescription, (projectGenerationContext) -> {
			assertThat(projectGenerationContext.getBeansOfType(String.class)).isEmpty();
			return null;
		});
	}

	@Test
	void outcomeWithNoAvailablePlatformVersion() {
		ProjectDescription projectDescription = new ProjectDescription();
		this.projectTester.generate(projectDescription, (projectGenerationContext) -> {
			assertThat(projectGenerationContext.getBeansOfType(String.class)).isEmpty();
			return null;
		});
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
	static class PlatformVersionTestConfiguration {

		@Bean
		@ConditionalOnPlatformVersion("[1.0.0.RELEASE, 2.0.0.M1)")
		public String first() {
			return "one";
		}

		@Bean
		@ConditionalOnPlatformVersion("2.0.0.M1")
		public String second() {
			return "two";
		}

	}

}
