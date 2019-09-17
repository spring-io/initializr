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
		new ProjectAssetTester().withIndentingWriterFactory().configure(new MutableProjectDescription(),
				(context) -> assertThat(context).hasSingleBean(IndentingWriterFactory.class));
	}

	@Test
	void testerWithExplicitProjectContributors(@TempDir Path directory) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName("test.text");
		ProjectStructure project = new ProjectAssetTester().withDirectory(directory)
				.withConfiguration(ContributorsConfiguration.class).generate(description);
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
