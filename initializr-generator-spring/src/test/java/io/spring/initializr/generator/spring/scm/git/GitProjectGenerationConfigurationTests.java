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

package io.spring.initializr.generator.spring.scm.git;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.io.TextTestUtils;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GitProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class GitProjectGenerationConfigurationTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(GitProjectGenerationConfiguration.class);

	@Test
	void gitIgnoreIsContributedToProject(@TempDir Path directory) {
		ProjectDescription description = new ProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		Path projectDirectory = this.projectTester.withDirectory(directory)
				.generate(description, (context) -> {
					GitIgnoreContributor contributor = context
							.getBean(GitIgnoreContributor.class);
					contributor.contribute(directory);
					return directory;
				});
		assertThat(projectDirectory.resolve(".gitignore")).isRegularFile();
	}

	@Test
	void gitIgnore() {
		ProjectDescription description = new ProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		assertThat(generateGitIgnore(description)).contains("### STS ###",
				"### IntelliJ IDEA ###", "### NetBeans ###", "### VS Code ###");
	}

	@Test
	void gitIgnoreGradle() {
		ProjectDescription description = new ProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		assertThat(generateGitIgnore(description))
				.contains(".gradle", "/build/", "!gradle/wrapper/gradle-wrapper.jar",
						"/out/")
				.doesNotContain("/target/", "!.mvn/wrapper/maven-wrapper.jar");
	}

	@Test
	void gitIgnoreMaven() {
		ProjectDescription description = new ProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		assertThat(generateGitIgnore(description))
				.contains("/target/", "!.mvn/wrapper/maven-wrapper.jar")
				.doesNotContain(".gradle", "!gradle/wrapper/gradle-wrapper.jar", "/out/");
	}

	private List<String> generateGitIgnore(ProjectDescription description) {
		return this.projectTester.generate(description, (context) -> {
			GitIgnore gitIgnore = context.getBean(GitIgnore.class);
			StringWriter out = new StringWriter();
			gitIgnore.write(new PrintWriter(out));
			return TextTestUtils.readAllLines(out.toString());
		});
	}

}
