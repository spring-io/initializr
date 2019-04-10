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

import java.util.Map;

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
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class))
						.containsOnlyKeys("first");
	}

	@Test
	void outcomeWithMatchingOpenRange() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.1.RELEASE"));
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class))
						.containsOnlyKeys("second");
	}

	@Test
	void outcomeWithMatchingStartOfOpenRange() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.0.M1"));
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class))
						.containsOnlyKeys("second");
	}

	@Test
	void outcomeWithNoMatch() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("0.1.0"));
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class))
						.isEmpty();
	}

	@Test
	void outcomeWithNoAvailablePlatformVersion() {
		ProjectDescription projectDescription = new ProjectDescription();
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class))
						.isEmpty();
	}

	@Test
	void outcomeWithSeveralRangesAndMatchingVersion() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class,
						OneOrTwoPlatformVersionTestConfiguration.class))
								.containsOnlyKeys("second", "firstOrSecond");
	}

	@Test
	void outcomeWithSeveralRangesAndNonMatchingVersion() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.0.M2"));
		assertThat(
				candidatesFor(projectDescription, PlatformVersionTestConfiguration.class,
						OneOrTwoPlatformVersionTestConfiguration.class))
								.containsOnlyKeys("second");
	}

	private Map<String, String> candidatesFor(ProjectDescription projectDescription,
			Class<?>... extraConfigurations) {
		return this.projectTester.withConfiguration(extraConfigurations).generate(
				projectDescription, (projectGenerationContext) -> projectGenerationContext
						.getBeansOfType(String.class));
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

	@Configuration
	static class OneOrTwoPlatformVersionTestConfiguration {

		@Bean
		@ConditionalOnPlatformVersion({ "[1.0.0.RELEASE, 2.0.0.M1)", "2.0.0.RELEASE" })
		public String firstOrSecond() {
			return "oneOrTwo";
		}

	}

}
