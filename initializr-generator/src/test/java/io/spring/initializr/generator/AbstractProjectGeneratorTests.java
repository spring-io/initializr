/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;
import io.spring.initializr.test.generator.GradleBuildAssert;
import io.spring.initializr.test.generator.PomAssert;
import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatcher;

import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Stephane Nicoll
 */
public abstract class AbstractProjectGeneratorTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	protected final ProjectGenerator projectGenerator;

	protected final ApplicationEventPublisher eventPublisher = mock(
			ApplicationEventPublisher.class);

	protected AbstractProjectGeneratorTests() {
		this(new ProjectGenerator());
	}

	protected AbstractProjectGeneratorTests(ProjectGenerator projectGenerator) {
		this.projectGenerator = projectGenerator;
	}

	@Before
	public void setup() throws IOException {
		Dependency web = Dependency.withId("web");
		web.getFacets().add("web");
		InitializrMetadata metadata = initializeTestMetadataBuilder()
				.addDependencyGroup("web", web).addDependencyGroup("test", "security",
						"data-jpa", "aop", "batch", "integration")
				.build();
		applyMetadata(metadata);
		this.projectGenerator.setEventPublisher(this.eventPublisher);
		this.projectGenerator
				.setRequestResolver(new ProjectRequestResolver(new ArrayList<>()));
		this.projectGenerator.setTmpdir(this.folder.newFolder().getAbsolutePath());
	}

	protected InitializrMetadataTestBuilder initializeTestMetadataBuilder() {
		return InitializrMetadataTestBuilder.withDefaults();
	}

	protected PomAssert generateMavenPom(ProjectRequest request) {
		request.setType("maven-build");
		String content = new String(this.projectGenerator.generateMavenPom(request));
		return new PomAssert(content).validateProjectRequest(request);
	}

	protected GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		request.setType("gradle-build");
		String content = new String(this.projectGenerator.generateGradleBuild(request));
		return new GradleBuildAssert(content).validateProjectRequest(request);
	}

	protected ProjectAssert generateProject(ProjectRequest request) {
		File dir = this.projectGenerator.generateProjectStructure(request);
		return new ProjectAssert(dir);
	}

	public ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest();
		request.initialize(this.projectGenerator.getMetadataProvider().get());
		request.getStyle().addAll(Arrays.asList(styles));
		return request;
	}

	protected void applyMetadata(InitializrMetadata metadata) {
		this.projectGenerator
				.setMetadataProvider(new SimpleInitializrMetadataProvider(metadata));
	}

	protected void verifyProjectSuccessfulEventFor(ProjectRequest request) {
		verify(this.eventPublisher, times(1))
				.publishEvent(argThat(new ProjectGeneratedEventMatcher(request)));
	}

	protected void verifyProjectFailedEventFor(ProjectRequest request, Exception ex) {
		verify(this.eventPublisher, times(1))
				.publishEvent(argThat(new ProjectFailedEventMatcher(request, ex)));
	}

	protected static class ProjectGeneratedEventMatcher
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

}
