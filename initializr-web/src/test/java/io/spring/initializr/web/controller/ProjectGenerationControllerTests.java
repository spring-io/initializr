/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectGenerationResult;
import io.spring.initializr.web.project.ProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ProjectGenerationController}.
 *
 * @author Moritz Halbritter
 */
class ProjectGenerationControllerTests {

	private final ProjectGenerationInvoker<ProjectRequest> invoker = mock();

	private final ProjectGenerationController<ProjectRequest> controller = new ProjectGenerationController<>(
			mock(InitializrMetadataProvider.class), this.invoker) {

		@Override
		public ProjectRequest projectRequest(Map<String, String> headers) {
			return new ProjectRequest();
		}

	};

	@TempDir
	private Path tempDir;

	private Path root;

	@BeforeEach
	void setup() throws IOException {
		this.root = Files.createDirectory(this.tempDir.resolve("project"));
		Files.writeString(this.root.resolve("pom.xml"), "<project/>");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		description.setArtifactId("demo");
		ProjectGenerationResult result = mock();
		given(result.getRootDirectory()).willReturn(this.root);
		given(result.getProjectDescription()).willReturn(description);
		given(this.invoker.invokeProjectStructureGeneration(any())).willReturn(result);
	}

	@Test
	void springZipCleansTempFiles() throws IOException {
		givenArchive(this.tempDir.resolve("project.zip"));
		assertThat(this.controller.springZip(new ProjectRequest()).getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(this.invoker).cleanTempFiles(this.root);
	}

	@Test
	void springTgzCleansTempFiles() throws IOException {
		givenArchive(this.tempDir.resolve("project.tar.gz"));
		assertThat(this.controller.springTgz(new ProjectRequest()).getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(this.invoker).cleanTempFiles(this.root);
	}

	@Test
	void springZipWhenArchiveFailsCleansTempFiles() {
		givenUnwritableArchive();
		assertThatException().isThrownBy(() -> this.controller.springZip(new ProjectRequest()));
		verify(this.invoker).cleanTempFiles(this.root);
	}

	@Test
	void springTgzWhenArchiveFailsCleansTempFiles() {
		givenUnwritableArchive();
		assertThatException().isThrownBy(() -> this.controller.springTgz(new ProjectRequest()));
		verify(this.invoker).cleanTempFiles(this.root);
	}

	private void givenArchive(Path archive) {
		given(this.invoker.createDistributionFile(any(), anyString())).willReturn(archive);
	}

	private void givenUnwritableArchive() {
		givenArchive(this.tempDir.resolve("missing").resolve("project.zip"));
	}

}
