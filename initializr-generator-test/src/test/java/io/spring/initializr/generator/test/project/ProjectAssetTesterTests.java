/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.project;

import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link ProjectAssetTester}.
 *
 * @author Stephane Nicoll
 */
class ProjectAssetTesterTests {

	@Test
	void testerHasNoRegisteredIndentingWriterFactoryByDefault() {
		new ProjectAssetTester().configure(new MutableProjectDescription(),
				(context) -> assertThat(context).doesNotHaveBean(IndentingWriterFactory.class));
	}

	@Test
	void testerWithIndentingWriterFactory() {
		new ProjectAssetTester().withIndentingWriterFactory()
			.configure(new MutableProjectDescription(),
					(context) -> assertThat(context).hasSingleBean(IndentingWriterFactory.class));
	}

	@Test
	void testerWithExplicitProjectContributors(@TempDir Path directory) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName("test.text");
		ProjectStructure project = new ProjectAssetTester().withDirectory(directory)
			.withConfiguration(ContributorsConfiguration.class)
			.generate(description);
		assertThat(project).filePaths().containsOnly("test.text", "test2.text");
	}

	@Test
	void testerWithContextFailureIsProperlyReported() {
		new ProjectAssetTester().withConfiguration(ContributorFailureConfiguration.class)
			.configure(new MutableProjectDescription(), (context) -> {
				assertThat(context).hasFailed();
				assertThat(context.getStartupFailure()).isInstanceOf(UnsatisfiedDependencyException.class);
				assertThat(context.getStartupFailure().getMessage()).doesNotContain("Should not be invoked");
			});
	}

	@Test
	void testerWithContextSuccessFailToAssertFailure() {
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> new ProjectAssetTester().withConfiguration(ContributorsConfiguration.class)
				.configure(new MutableProjectDescription(), (context) -> assertThat(context).hasFailed()));
	}

	@Configuration
	static class ContributorsConfiguration {

		@Bean
		ProjectContributor contributor1(ProjectDescription description) {
			return (projectDirectory) -> Files.createFile(projectDirectory.resolve(description.getName()));
		}

		@Bean
		ProjectContributor contributor2() {
			return (projectDirectory) -> Files.createFile(projectDirectory.resolve("test2.text"));
		}

	}

	@Configuration
	static class ContributorFailureConfiguration {

		@Bean
		ProjectContributor failContributor(MustacheTemplateRenderer templateRenderer) {
			throw new IllegalStateException("Should not be invoked");
		}

	}

}
