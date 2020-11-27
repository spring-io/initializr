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

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnGradleVersion}.
 *
 * @author Stephane Nicoll
 */
public class ConditionalOnGradleVersionTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(GradleVersionTestConfiguration.class);

	@Test
	void outcomeWithSpringBoot20() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.9.RELEASE"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(String.class)
				.getBean(String.class).isEqualTo("testGradle4"));
	}

	@Test
	void outcomeWithSpringBoot21() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.3.RELEASE"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(String.class)
				.getBean(String.class).isEqualTo("testGradle5"));
	}

	@Test
	void outcomeWithNoMatch() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.0.0.RELEASE"));
		this.projectTester.configure(description, (context) -> assertThat(context).doesNotHaveBean(String.class));
	}

	@Test
	void outcomeWithNoAvailableSpringBootVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		this.projectTester.configure(description, (context) -> assertThat(context).doesNotHaveBean(String.class));
	}

	@Test
	void outcomeWithSpringBoot20AndMultipleGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.9.RELEASE"));
		this.projectTester.withConfiguration(Gradle4Or5TestConfiguration.class).configure(description,
				(context) -> assertThat(context).getBeanNames(String.class).containsOnly("gradle4", "gradle4Or5"));
	}

	@Test
	void outcomeWithSpringBoot24AndMultipleNonMatchingGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		this.projectTester.withConfiguration(Gradle4Or5TestConfiguration.class).configure(description,
				(context) -> assertThat(context).getBeanNames(String.class).containsOnly("gradle6"));
	}

	@Configuration
	static class GradleVersionTestConfiguration {

		@Bean
		@ConditionalOnGradleVersion("4")
		String gradle4() {
			return "testGradle4";
		}

		@Bean
		@ConditionalOnGradleVersion("5")
		String gradle5() {
			return "testGradle5";
		}

		@Bean
		@ConditionalOnGradleVersion("6")
		String gradle6() {
			return "testGradle6";
		}

	}

	@Configuration
	static class Gradle4Or5TestConfiguration {

		@Bean
		@ConditionalOnGradleVersion({ "4", "5" })
		String gradle4Or5() {
			return "testGradle4Or5";
		}

	}

}
