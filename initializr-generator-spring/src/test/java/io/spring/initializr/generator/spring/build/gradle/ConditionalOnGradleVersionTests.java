/*
 * Copyright 2012-2021 the original author or authors.
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
	void outcomeWithSpringBoot23() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.3.10.RELEASE"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(String.class)
				.getBean(String.class).isEqualTo("testGradle6"));
	}

	@Test
	void outcomeWithSpringBoot24() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(String.class)
				.getBean(String.class).isEqualTo("testGradle6"));
	}

	@Test
	void outcomeWithSpringBoot25() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.5.1"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(String.class)
				.getBean(String.class).isEqualTo("testGradle7"));
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
	void outcomeWithSpringBoot24AndMultipleGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		this.projectTester.withConfiguration(Gradle6Or7TestConfiguration.class).configure(description,
				(context) -> assertThat(context).getBeanNames(String.class).containsOnly("gradle6", "gradle6Or7"));
	}

	@Configuration
	static class GradleVersionTestConfiguration {

		@Bean
		@ConditionalOnGradleVersion("6")
		String gradle6() {
			return "testGradle6";
		}

		@Bean
		@ConditionalOnGradleVersion("7")
		String gradle7() {
			return "testGradle7";
		}

	}

	@Configuration
	static class Gradle6Or7TestConfiguration {

		@Bean
		@ConditionalOnGradleVersion({ "6", "7" })
		String gradle6Or7() {
			return "testGradle6Or7";
		}

	}

}
