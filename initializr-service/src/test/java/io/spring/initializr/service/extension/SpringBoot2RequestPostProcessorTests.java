/*
 * Copyright 2012-2017 the original author or authors.
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

import java.util.Arrays;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.test.generator.GradleBuildAssert;
import io.spring.initializr.test.generator.PomAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link SpringBoot2RequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SpringBoot2RequestPostProcessorTests {

	@Autowired
	private ProjectGenerator projectGenerator;

	@Autowired
	private InitializrMetadataProvider metadataProvider;

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

	private PomAssert generateMavenPom(ProjectRequest request) {
		request.setType("maven-build");
		String content = new String(projectGenerator.generateMavenPom(request));
		return new PomAssert(content);
	}

	private GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		request.setType("gradle-build");
		String content = new String(projectGenerator.generateGradleBuild(request));
		return new GradleBuildAssert(content);
	}

	private ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadataProvider.get());
		request.getStyle().addAll(Arrays.asList(styles));
		return request;
	}

}
