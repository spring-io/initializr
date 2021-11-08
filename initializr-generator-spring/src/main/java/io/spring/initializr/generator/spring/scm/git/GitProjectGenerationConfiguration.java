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

package io.spring.initializr.generator.spring.scm.git;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for Git-related contributions to a generated project.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Yves Galvão
 */
@ProjectGenerationConfiguration
public class GitProjectGenerationConfiguration {

	@Bean
	public GitIgnoreContributor gitIgnoreContributor(GitIgnore gitIgnore) {
		return new GitIgnoreContributor(gitIgnore);
	}

	@Bean
	public GitIgnore gitIgnore(ObjectProvider<GitIgnoreCustomizer> gitIgnoreCustomizers) {
		GitIgnore gitIgnore = new GitIgnore();
		gitIgnoreCustomizers.orderedStream().forEach((customizer) -> customizer.customize(gitIgnore));
		return gitIgnore;
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public GitIgnoreCustomizer mavenGitIgnoreCustomizer() {
		return (gitIgnore) -> {
			gitIgnore.getGitIgnoreSection(GitEnum.GENERAL).add("target/", "!.mvn/wrapper/maven-wrapper.jar",
					"!**/src/main/**/target/", "!**/src/test/**/target/");
			gitIgnore.getGitIgnoreSection(GitEnum.NET_BEANS).add("build/", "!**/src/main/**/build/",
					"!**/src/test/**/build/");
		};
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public GitIgnoreCustomizer gradleGitIgnoreCustomizer() {
		return (gitIgnore) -> {
			gitIgnore.getGitIgnoreSection(GitEnum.GENERAL).add(".gradle", "build/",
					"!gradle/wrapper/gradle-wrapper.jar", "!**/src/main/**/build/", "!**/src/test/**/build/");
			gitIgnore.getGitIgnoreSection(GitEnum.INTELLIJ_IDEA).add("out/", "!**/src/main/**/out/",
					"!**/src/test/**/out/");
			gitIgnore.getGitIgnoreSection(GitEnum.STS).add("bin/", "!**/src/main/**/bin/", "!**/src/test/**/bin/");
		};
	}

}
