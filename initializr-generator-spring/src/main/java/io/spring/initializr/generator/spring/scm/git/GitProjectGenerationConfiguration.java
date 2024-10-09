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
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
public class GitProjectGenerationConfiguration {

	@Bean
	public GitIgnoreContributor gitIgnoreContributor(GitIgnore gitIgnore) {
		return new GitIgnoreContributor(gitIgnore);
	}

	@Bean
	public GitIgnore gitIgnore(ObjectProvider<GitIgnoreCustomizer> gitIgnoreCustomizers) {
		GitIgnore gitIgnore = createGitIgnore();
		gitIgnoreCustomizers.orderedStream().forEach((customizer) -> customizer.customize(gitIgnore));
		return gitIgnore;
	}

	@Bean
	GitAttributesContributor gitAttributesContributor(GitAttributes gitAttributes) {
		return new GitAttributesContributor(gitAttributes);
	}

	@Bean
	GitAttributes gitAttributes(ObjectProvider<GitAttributesCustomizer> gitAttributesCustomizers) {
		GitAttributes gitAttributes = new GitAttributes();
		gitAttributesCustomizers.orderedStream().forEach((customizer) -> customizer.customize(gitAttributes));
		return gitAttributes;
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public GitIgnoreCustomizer mavenGitIgnoreCustomizer() {
		return (gitIgnore) -> {
			gitIgnore.getGeneral()
				.add("target/", "!.mvn/wrapper/maven-wrapper.jar", "!**/src/main/**/target/",
						"!**/src/test/**/target/");
			gitIgnore.getNetBeans().add("build/", "!**/src/main/**/build/", "!**/src/test/**/build/");
		};
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public GitIgnoreCustomizer gradleGitIgnoreCustomizer() {
		return (gitIgnore) -> {
			gitIgnore.getGeneral()
				.add(".gradle", "build/", "!gradle/wrapper/gradle-wrapper.jar", "!**/src/main/**/build/",
						"!**/src/test/**/build/");
			gitIgnore.getIntellijIdea().add("out/", "!**/src/main/**/out/", "!**/src/test/**/out/");
			gitIgnore.getSts().add("bin/", "!**/src/main/**/bin/", "!**/src/test/**/bin/");
		};
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public GitAttributesCustomizer mavenGitAttributesCustomizer() {
		return (gitAttributes) -> {
			gitAttributes.add("/mvnw", "text", "eol=lf");
			gitAttributes.add("*.cmd", "text", "eol=crlf");
		};
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public GitAttributesCustomizer gradleGitAttributesCustomizer() {
		return (gitAttributes) -> {
			gitAttributes.add("/gradlew", "text", "eol=lf");
			gitAttributes.add("*.bat", "text", "eol=crlf");
			gitAttributes.add("*.jar", "binary");
		};
	}

	private GitIgnore createGitIgnore() {
		GitIgnore gitIgnore = new GitIgnore();
		gitIgnore.getSts()
			.add(".apt_generated", ".classpath", ".factorypath", ".project", ".settings", ".springBeans",
					".sts4-cache");
		gitIgnore.getIntellijIdea().add(".idea", "*.iws", "*.iml", "*.ipr");
		gitIgnore.getNetBeans().add("/nbproject/private/", "/nbbuild/", "/dist/", "/nbdist/", "/.nb-gradle/");
		gitIgnore.getVscode().add(".vscode/");
		return gitIgnore;
	}

}
