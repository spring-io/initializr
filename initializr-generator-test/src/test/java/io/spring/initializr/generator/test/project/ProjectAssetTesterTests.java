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
import java.util.Map;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectAssetTester}.
 *
 * @author Stephane Nicoll
 */
class ProjectAssetTesterTests {

	@Test
	void testerHasNoRegisteredIndentingWriterFactoryByDefault() {
		Map<String, IndentingWriterFactory> contributors = new ProjectAssetTester().generate(
				new MutableProjectDescription(), (context) -> context.getBeansOfType(IndentingWriterFactory.class));
		assertThat(contributors).isEmpty();
	}

	@Test
	void testerWithIndentingWriterFactory() {
		new ProjectAssetTester().withIndentingWriterFactory().generate(new MutableProjectDescription(),
				(context) -> assertThat(context.getBeansOfType(IndentingWriterFactory.class)).hasSize(1));
	}

	@Test
	void testerWithExplicitProjectContributors(@TempDir Path directory) {
		ProjectStructure project = new ProjectAssetTester().withDirectory(directory)
				.withConfiguration(ContributorsConfiguration.class).generate(new MutableProjectDescription());
		assertThat(project).filePaths().containsOnly("test.text", "test2.text");
	}

	@Configuration
	static class ContributorsConfiguration {

		@Bean
		ProjectContributor contributor1() {
			return (projectDirectory) -> Files.createFile(projectDirectory.resolve("test.text"));
		}

		@Bean
		ProjectContributor contributor2() {
			return (projectDirectory) -> Files.createFile(projectDirectory.resolve("test2.text"));
		}

	}

}
