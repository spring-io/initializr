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

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGeneratorTester}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class ProjectGeneratorTesterTests {

	@Test
	void testerHasNoRegisteredContributorByDefault() {
		Map<String, ProjectContributor> contributors = new ProjectGeneratorTester().generate(
				new MutableProjectDescription(), (context) -> context.getBeansOfType(ProjectContributor.class));
		assertThat(contributors).isEmpty();
	}

	@Test
	void testerWithDescriptionCustomizer() {
		Version platformVersion = Version.parse("2.1.0.RELEASE");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackageName("com.example.test");
		ProjectDescription customizedDescription = new ProjectGeneratorTester().withDescriptionCustomizer((desc) -> {
			desc.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
			desc.setPackageName("com.example.another");
		}).generate(description, (context) -> context.getBean(ProjectDescription.class));
		assertThat(customizedDescription.getPlatformVersion()).isEqualTo(platformVersion);
		assertThat(customizedDescription.getPackageName()).isEqualTo("com.example.another");
	}

	@Test
	void testerWithExplicitProjectContributors(@TempDir Path directory) {
		ProjectGeneratorTester tester = new ProjectGeneratorTester().withDirectory(directory)
				.withContextInitializer((context) -> {
					context.registerBean("contributor1", ProjectContributor.class,
							() -> (projectDirectory) -> Files.createFile(projectDirectory.resolve("test.text")));
					context.registerBean("contributor2", ProjectContributor.class, () -> (projectDirectory) -> {
						Path subDir = projectDirectory.resolve("src/main/test");
						Files.createDirectories(subDir);
						Files.createFile(subDir.resolve("Test.src"));
					});
				});
		ProjectStructure project = tester.generate(new MutableProjectDescription());
		assertThat(project).filePaths().containsOnly("test.text", "src/main/test/Test.src");
	}

}
