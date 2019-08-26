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

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectGenerationController} with custom environment.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-env" })
class ProjectGenerationControllerCustomEnvIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void generateProjectWithInvalidName() {
		ProjectStructure project = downloadZip("/starter.zip?style=data-jpa&name=Invalid");
		assertThat(project).containsFiles("src/main/java/com/example/demo/FooBarApplication.java",
				"src/test/java/com/example/demo/FooBarApplicationTests.java");
		assertThat(project).doesNotContainFiles("src/main/java/com/example/demo/DemoApplication.java",
				"src/test/java/com/example/demo/DemoApplicationTests.java");
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild().hasDependenciesSize(2)
				.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
				.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

}
