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

package io.spring.initializr.web.controller.custom;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.controller.custom.ProjectGenerationControllerCustomRequestIntegrationTests.CustomProjectGenerationConfiguration;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for a {@link ProjectGenerationController} that maps to a custom
 * request.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@Import(CustomProjectGenerationConfiguration.class)
public class ProjectGenerationControllerCustomRequestIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	void createProjectWithCustomFlagEnabled() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&customFlag=true");
		assertThat(project).containsFiles("custom.txt");
	}

	@Test
	void createProjectWithCustomFlagDisabled() {
		ProjectStructure project = downloadZip("/starter.zip?dependencies=web&customFlag=false");
		assertThat(project).doesNotContainFiles("custom.txt");
	}

	@Test
	void createProjectWithOverriddenRequestParams() {
		ProjectStructure project = downloadZip("/starter.zip?groupId=com.acme&artifactId=test");
		assertThat(project).containsFiles("src/main/java/org/example/custom/CustomApp.java",
				"src/test/java/org/example/custom/CustomAppTests.java");
		assertThat(project).doesNotContainDirectories("src/main/java/com", "src/test/java/com");
		assertThat(project).doesNotContainFiles("custom.txt");
	}

	@Configuration
	static class CustomProjectGenerationConfiguration {

		@Bean
		CustomProjectGenerationController customProjectGenerationController(InitializrMetadataProvider metadataProvider,
				ProjectGenerationInvoker projectGenerationInvoker) {
			return new CustomProjectGenerationController(metadataProvider, projectGenerationInvoker);
		}

		@Bean
		ProjectRequestToDescriptionConverter customProjectRequestToDescriptionConverter() {
			return new CustomProjectRequestToDescriptionConverter();
		}

	}

	static class CustomProjectRequestToDescriptionConverter implements ProjectRequestToDescriptionConverter {

		@Override
		public ProjectDescription convert(ProjectRequest request, InitializrMetadata metadata) {
			CustomProjectRequest customRequest = (CustomProjectRequest) request;
			CustomProjectDescription description = new CustomProjectDescription();
			new DefaultProjectRequestToDescriptionConverter().convert(request, description, metadata);
			description.setCustomFlag(customRequest.isCustomFlag());
			// Override attributes for test purposes
			description.setPackageName("org.example.custom");
			description.setApplicationName("CustomApp");
			return description;
		}

	}

}
