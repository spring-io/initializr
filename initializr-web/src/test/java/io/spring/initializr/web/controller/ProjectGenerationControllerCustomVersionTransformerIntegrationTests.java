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

package io.spring.initializr.web.controller;

import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectGenerationController} with a custom platform
 * version compatibility range.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@TestPropertySource(properties = "initializr.env.platform.v2-format-compatibility-range=2.4.0-M1")
class ProjectGenerationControllerCustomVersionTransformerIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	void projectGenerationInvokeProjectRequestVersionTransformer() {
		ProjectStructure project = downloadZip("/starter.zip?bootVersion=2.4.0.RELEASE");
		assertThat(project).mavenBuild().hasParent("org.springframework.boot", "spring-boot-starter-parent", "2.4.0");
	}

}
