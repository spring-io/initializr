/*
 * Copyright 2012 - present the original author or authors.
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
	void outcomeWithSpringBoot40() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("4.0.0"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(String.class)
					.getBean(String.class)
					.isEqualTo("testGradle9"));
	}

	@Test
	void outcomeWithNoAvailableSpringBootVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		this.projectTester.configure(description, (context) -> assertThat(context).doesNotHaveBean(String.class));
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

		@Bean
		@ConditionalOnGradleVersion("9")
		String gradle9() {
			return "testGradle9";
		}

	}

}
