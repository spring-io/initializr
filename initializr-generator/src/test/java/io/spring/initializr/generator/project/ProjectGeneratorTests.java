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

package io.spring.initializr.generator.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.test.project.ProjectGeneratorTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGenerator}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class ProjectGeneratorTests {

	private final ProjectGeneratorTester projectTester = new ProjectGeneratorTester()
			.withDescriptionCustomizer((description) -> {
				description.setBuildSystem(new MavenBuildSystem());
				description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
			});

	@Test
	void generateInvokedProcessor() {
		ProjectDescription description = new ProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		Version platformVersion = Version.parse("2.1.0.RELEASE");
		description.setPackageName("com.example.test");
		ResolvedProjectDescription resolvedProjectDescription = this.projectTester
				.generate(description,
						(projectGenerationContext) -> projectGenerationContext
								.getBean(ResolvedProjectDescription.class));
		assertThat(resolvedProjectDescription.getPlatformVersion())
				.isEqualTo(platformVersion);
		assertThat(resolvedProjectDescription.getPackageName())
				.isEqualTo("com.example.test");
	}

	@Test
	void generateInvokesCustomizers() {
		ProjectGeneratorTester tester = this.projectTester
				.withContextInitializer((context) -> {
					context.registerBean("customizer1",
							TestProjectDescriptionCustomizer.class,
							() -> new TestProjectDescriptionCustomizer(5,
									(description) -> description.setName("Test")));
					context.registerBean("customizer2",
							TestProjectDescriptionCustomizer.class,
							() -> new TestProjectDescriptionCustomizer(3,
									(description) -> {
										description.setName("First");
										description.setGroupId("com.acme");
									}));
				});
		ProjectDescription description = new ProjectDescription();
		description.setGroupId("com.example.demo");
		description.setName("Original");

		ResolvedProjectDescription resolvedProjectDescription = tester.generate(
				description, (projectGenerationContext) -> projectGenerationContext
						.getBean(ResolvedProjectDescription.class));
		assertThat(resolvedProjectDescription.getGroupId()).isEqualTo("com.acme");
		assertThat(resolvedProjectDescription.getName()).isEqualTo("Test");
	}

	@Test
	void generateInvokeProjectContributors(@TempDir Path directory) {
		ProjectGeneratorTester tester = this.projectTester.withDirectory(directory)
				.withContextInitializer((context) -> {
					context.registerBean("contributor1", ProjectContributor.class,
							() -> (projectDirectory) -> Files
									.createFile(projectDirectory.resolve("test.text")));
					context.registerBean("contributor2", ProjectContributor.class,
							() -> (projectDirectory) -> {
								Path subDir = projectDirectory.resolve("src/main/test");
								Files.createDirectories(subDir);
								Files.createFile(subDir.resolve("Test.src"));
							});
				});
		ProjectStructure projectStructure = tester.generate(new ProjectDescription());
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.containsOnly("test.text", "src/main/test/Test.src");
	}

	private static class TestProjectDescriptionCustomizer
			implements ProjectDescriptionCustomizer {

		private final Integer order;

		private final Consumer<ProjectDescription> projectDescription;

		TestProjectDescriptionCustomizer(Integer order,
				Consumer<ProjectDescription> projectDescription) {
			this.order = order;
			this.projectDescription = projectDescription;
		}

		@Override
		public void customize(ProjectDescription description) {
			this.projectDescription.accept(description);
		}

		@Override
		public int getOrder() {
			return this.order;
		}

	}

}
