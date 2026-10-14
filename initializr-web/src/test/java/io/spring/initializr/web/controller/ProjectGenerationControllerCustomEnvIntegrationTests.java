/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for {@link ProjectGenerationController} with custom environment.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-env" })
class ProjectGenerationControllerCustomEnvIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void generateProjectWithInvalidName() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=data-jpa&name=Invalid");
		assertThat(project).containsFiles("src/main/java/com/example/demo/FooBarApplication.java",
				"src/test/java/com/example/demo/FooBarApplicationTests.java");
		assertThat(project).doesNotContainFiles("src/main/java/com/example/demo/DemoApplication.java",
				"src/test/java/com/example/demo/DemoApplicationTests.java");
		assertDoesNotHaveWebResources(project);
		assertThat(project).mavenBuild()
			.hasDependenciesSize(2)
			.hasDependency(Dependency.createSpringBootStarter("data-jpa"))
			.hasDependency(Dependency.createSpringBootStarter("test", Dependency.SCOPE_TEST));
	}

	@Test
	void generateProjectWithUnsupportedPlatformVersion() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/starter.zip?bootVersion=1.5.12.RELEASE", byte[].class, null, (String[]) null))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getMessage()).contains("Invalid Spring Boot version",
						"Spring Boot compatibility range is >=2.0.0.RELEASE");
			});
	}

	@Test
	void getDependenciesMetadataWithUnsupportedPlatformVersion() {
		assertThatExceptionOfType(HttpClientErrorException.class)
			.isThrownBy(() -> execute("/dependencies?bootVersion=1.5.12.RELEASE", String.class,
					"application/vnd.initializr.v2.1+json", "application/json"))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getMessage()).contains("Invalid Spring Boot version",
						"Spring Boot compatibility range is >=2.0.0.RELEASE");
			});
	}

}
