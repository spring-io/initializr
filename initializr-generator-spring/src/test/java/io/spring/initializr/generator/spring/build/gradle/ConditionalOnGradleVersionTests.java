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
class ConditionalOnGradleVersionTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
		.withConfiguration(GradleVersionTestConfiguration.class);

	@Test
	@Deprecated
	void outcomeWithSpringBoot23() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.3.10.RELEASE"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle6"));
	}

	@Test
	@Deprecated
	void outcomeWithSpringBoot24() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle6"));
	}

	@Test
	void outcomeWithSpringBoot25() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.5.1"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle7"));
	}

	@Test
	void outcomeWithSpringBoot26() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.6.1"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle7"));
	}

	@Test
	void outcomeWithSpringBootEarly27() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.7.9"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle7"));
	}

	@Test
	void outcomeWithSpringBootLate27() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.7.10"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle8"));
	}

	@Test
	void outcomeWithSpringBoot30() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("3.0.0"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle8"));
	}

	@Test
	void outcomeWithSpringBoot31() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("3.1.0"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle8"));
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
		description.setPlatformVersion(Version.parse("2.7.0"));
		this.projectTester.withConfiguration(Gradle7Or8TestConfiguration.class)
			.configure(description,
					(context) -> assertThat(context).getBeanNames(String.class).containsOnly("gradle7", "gradle7Or8"));
	}

	@Configuration
	static class GradleVersionTestConfiguration {

		@Bean
		@ConditionalOnGradleVersion("6")
		@Deprecated
		String gradle6() {
			return "testGradle6";
		}

		@Bean
		@ConditionalOnGradleVersion("7")
		String gradle7() {
			return "testGradle7";
		}

		@Bean
		@ConditionalOnGradleVersion("8")
		String gradle8() {
			return "testGradle8";
		}

	}

	@Configuration
	static class Gradle7Or8TestConfiguration {

		@Bean
		@ConditionalOnGradleVersion({ "7", "8" })
		String gradle7Or8() {
			return "testGradle7Or8";
		}

	}

}
