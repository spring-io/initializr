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

package io.spring.initializr.web.project;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.spring.test.ProjectAssert;
import io.spring.initializr.generator.spring.test.build.GradleBuildAssert;
import io.spring.initializr.generator.spring.test.build.PomAssert;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatcher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ProjectGenerationInvoker}.
 *
 * @author Madhura Bhave
 */
public class ProjectGenerationInvokerTests {

	private static final InitializrMetadata metadata = InitializrMetadataTestBuilder
			.withDefaults().build();

	private ProjectGenerationInvoker invoker;

	private AnnotationConfigApplicationContext context;

	private final ApplicationEventPublisher eventPublisher = mock(
			ApplicationEventPublisher.class);

	@BeforeEach
	void setup() {
		setupContext();
		ProjectRequestToDescriptionConverter converter = new ProjectRequestToDescriptionConverter();
		this.invoker = new ProjectGenerationInvoker(this.context, this.eventPublisher,
				converter);
	}

	@AfterEach
	void cleanup() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void invokeProjectStructureGeneration() {
		WebProjectRequest request = new WebProjectRequest();
		request.setType("maven-project");
		request.initialize(metadata);
		ProjectGenerationResult result = this.invoker
				.invokeProjectStructureGeneration(request);
		new ProjectAssert(result.getRootDirectory()).isJavaProject();
		File file = result.getRootDirectory().toFile();
		Map<String, List<File>> tempFiles = (Map<String, List<File>>) ReflectionTestUtils
				.getField(this.invoker, "temporaryFiles");
		assertThat(tempFiles.get(file.getName())).contains(file);
		verifyProjectSuccessfulEventFor(request);
	}

	@Test
	void invokeProjectStructureGenerationFailureShouldPublishFailureEvent() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(metadata);
		request.setType("foo-bar");
		try {
			this.invoker.invokeProjectStructureGeneration(request);
		}
		catch (Exception ex) {
			verifyProjectFailedEventFor(request, ex);
		}
	}

	@Test
	void invokeBuildGenerationForMavenBuild() {
		WebProjectRequest request = new WebProjectRequest();
		request.setType("maven-project");
		request.initialize(metadata);
		byte[] bytes = this.invoker.invokeBuildGeneration(request);
		String content = new String(bytes);
		new PomAssert(content).hasGroupId(request.getGroupId())
				.hasArtifactId(request.getArtifactId()).hasVersion(request.getVersion())
				.doesNotHaveNode("/project/packaging").hasName(request.getName())
				.hasDescription(request.getDescription())
				.hasJavaVersion(request.getJavaVersion())
				.hasSpringBootParent(request.getBootVersion());
		verifyProjectSuccessfulEventFor(request);
	}

	@Test
	void invokeBuildGenerationForGradleBuild() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(metadata);
		request.setType("gradle-project");
		byte[] bytes = this.invoker.invokeBuildGeneration(request);
		String content = new String(bytes);
		new GradleBuildAssert(content).hasVersion(request.getVersion())
				.hasSpringBootPlugin(request.getBootVersion())
				.hasJavaVersion(request.getJavaVersion());
		verifyProjectSuccessfulEventFor(request);
	}

	@Test
	void invokeBuildGenerationFailureShouldPublishFailureEvent() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(metadata);
		request.setType("foo-bar");
		try {
			this.invoker.invokeBuildGeneration(request);
		}
		catch (Exception ex) {
			verifyProjectFailedEventFor(request, ex);
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void createDistributionDirectory(@TempDir Path tempDir) {
		ProjectRequest request = new ProjectRequest();
		request.setType("gradle-project");
		File dir = tempDir.toFile();
		File distributionFile = this.invoker.createDistributionFile(dir, ".zip");
		assertThat(distributionFile.toString()).isEqualTo(dir.toString() + ".zip");
		Map<String, List<File>> tempFiles = (Map<String, List<File>>) ReflectionTestUtils
				.getField(this.invoker, "temporaryFiles");
		assertThat(tempFiles.get(dir.getName())).contains(distributionFile);
	}

	@Test
	void cleanupTempFilesShouldOnlyCleanupSpecifiedDir() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(metadata);
		request.setType("gradle-project");
		ProjectGenerationResult result = this.invoker
				.invokeProjectStructureGeneration(request);
		File file = result.getRootDirectory().toFile();
		this.invoker.cleanTempFiles(file);
		assertThat(file.listFiles()).isNull();
	}

	private void setupContext() {
		InitializrMetadataProvider metadataProvider = mock(
				InitializrMetadataProvider.class);
		given(metadataProvider.get())
				.willReturn(InitializrMetadataTestBuilder.withDefaults().build());
		this.context = new AnnotationConfigApplicationContext();
		this.context.register(TestConfiguration.class);
		this.context.refresh();
	}

	protected void verifyProjectSuccessfulEventFor(ProjectRequest request) {
		verify(this.eventPublisher, times(1))
				.publishEvent(argThat(new ProjectGeneratedEventMatcher(request)));
	}

	protected void verifyProjectFailedEventFor(ProjectRequest request, Exception ex) {
		verify(this.eventPublisher, times(1))
				.publishEvent(argThat(new ProjectFailedEventMatcher(request, ex)));
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		public IndentingWriterFactory factory() {
			return IndentingWriterFactory.create(new SimpleIndentStrategy("\t"));
		}

		@Bean
		public MustacheTemplateRenderer templateRenderer() {
			return new MustacheTemplateRenderer("classpath:/templates");
		}

		@Bean
		public ProjectDirectoryFactory projectDirectoryFactory() {
			return (description) -> Files.createTempDirectory("project-");
		}

		@Bean
		public InitializrMetadataProvider initializrMetadataProvider() {
			return () -> metadata;
		}

	}

	private static class ProjectFailedEventMatcher
			implements ArgumentMatcher<ProjectFailedEvent> {

		private final ProjectRequest request;

		private final Exception cause;

		ProjectFailedEventMatcher(ProjectRequest request, Exception cause) {
			this.request = request;
			this.cause = cause;
		}

		@Override
		public boolean matches(ProjectFailedEvent event) {
			return this.request.equals(event.getProjectRequest())
					&& this.cause.equals(event.getCause());
		}

	}

	private static class ProjectGeneratedEventMatcher
			implements ArgumentMatcher<ProjectGeneratedEvent> {

		private final ProjectRequest request;

		ProjectGeneratedEventMatcher(ProjectRequest request) {
			this.request = request;
		}

		@Override
		public boolean matches(ProjectGeneratedEvent event) {
			return this.request.equals(event.getProjectRequest());
		}

	}

}
