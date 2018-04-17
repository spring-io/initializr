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

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import org.junit.Test;

/**
 * Tests for {@link SpringBoot2RequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
public class SpringBoot2RequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void java8IsMandatoryMaven() {
		ProjectRequest request = createProjectRequest("web");
		request.setBootVersion("2.0.0.BUILD-SNAPSHOT");
		request.setJavaVersion("1.7");
		generateMavenPom(request).hasJavaVersion("1.8");
	}

	@Test
	public void java8IsMandatoryGradle() {
		ProjectRequest request = createProjectRequest("data-jpa");
		request.setBootVersion("2.0.0.M3");
		request.setJavaVersion("1.7");
		generateGradleBuild(request).hasJavaVersion("1.8");
	}

	@Test
	public void java9CanBeUsedMaven() {
		ProjectRequest request = createProjectRequest("web");
		request.setBootVersion("2.0.0.BUILD-SNAPSHOT");
		request.setJavaVersion("9");
		generateMavenPom(request).hasJavaVersion("9");
	}

	@Test
	public void java9CanBeUsedGradle() {
		ProjectRequest request = createProjectRequest("data-jpa");
		request.setBootVersion("2.0.0.M3");
		request.setJavaVersion("9");
		generateGradleBuild(request).hasJavaVersion("9");
	}

	@Test
	public void java10CanBeUsedMaven() {
		ProjectRequest request = createProjectRequest("web");
		request.setBootVersion("2.1.0.BUILD-SNAPSHOT");
		request.setJavaVersion("10");
		generateMavenPom(request).hasJavaVersion("10");
	}

	@Test
	public void java10CanBeUsedGradle() {
		ProjectRequest request = createProjectRequest("data-jpa");
		request.setBootVersion("2.0.2.RELEASE");
		request.setJavaVersion("10");
		generateGradleBuild(request).hasJavaVersion("10");
	}

}
