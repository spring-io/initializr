/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test contributors.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
public class TestProjectGenerationConfiguration {

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public ProjectContributor testArtifactIdContributor(ProjectDescription description) {
		return (projectRoot) -> {
			Path testFile = projectRoot.resolve("artifact-id.txt");
			Files.createFile(testFile);
			String artifactId = description.getArtifactId();
			assertThat(artifactId).isNotNull();
			FileCopyUtils.copy(artifactId, Files.newBufferedWriter(testFile));
		};
	}

}
