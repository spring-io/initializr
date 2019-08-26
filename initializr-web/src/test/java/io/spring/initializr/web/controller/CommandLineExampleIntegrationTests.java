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

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.buildsystem.maven.MavenBuildAssert;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validate that the "raw" HTTP commands that are described in the command-line help
 * works. If anything needs to be updated here, please double check the
 * "cli/curl-examples.mustache" as it may need an update as well. This is also a good
 * indicator of a non backward compatible change.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class CommandLineExampleIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void generateDefaultProject() {
		ProjectStructure project = downloadZip("/starter.zip");
		assertDefaultProject(project);
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild().hasDependency(Dependency.createSpringBootStarter(""))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST))
				.hasDependenciesSize(2);
	}

	@Test
	void generateWebProjectWithJava8() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&javaVersion=1.8");
		assertDefaultProject(project);
		assertHasWebResources(project);
		assertThat(project).mavenBuild().hasProperty("java.version", "1.8")
				.hasDependency(Dependency.createSpringBootStarter("web"))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST))
				.hasDependenciesSize(2);
	}

	@Test
	void generateWebDataJpaGradleProject() {
		ProjectStructure project = downloadTgz(
				"/starter.tgz?dependencies=web,data-jpa&type=gradle-project&baseDir=my-dir").resolveModule("my-dir");
		assertHasWebResources(project);
		assertThat(project).groovyDslGradleBuild().contains("spring-boot-starter-web")
				.contains("spring-boot-starter-data-jpa");
	}

	@Test
	void generateMavenPomWithWarPackaging() {
		ResponseEntity<String> response = getRestTemplate().getForEntity(createUrl("/pom.xml?packaging=war"),
				String.class);
		MavenBuildAssert pomAssert = new MavenBuildAssert(response.getBody());
		pomAssert.hasPackaging("war");
	}

}
