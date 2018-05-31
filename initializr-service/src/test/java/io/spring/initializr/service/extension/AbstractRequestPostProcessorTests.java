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

import java.util.Arrays;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.test.generator.GradleBuildAssert;
import io.spring.initializr.test.generator.PomAssert;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base test class for {@link ProjectRequestPostProcessor} implementations.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public abstract class AbstractRequestPostProcessorTests {

	@Autowired
	private ProjectGenerator projectGenerator;

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	protected Dependency getDependency(String id) {
		return this.metadataProvider.get().getDependencies().get(id);
	}

	protected PomAssert generateMavenPom(ProjectRequest request) {
		request.setType("maven-build");
		String content = new String(this.projectGenerator.generateMavenPom(request));
		return new PomAssert(content);
	}

	protected GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		request.setType("gradle-build");
		String content = new String(this.projectGenerator.generateGradleBuild(request));
		return new GradleBuildAssert(content);
	}

	protected ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest();
		request.initialize(this.metadataProvider.get());
		request.getStyle().addAll(Arrays.asList(styles));
		return request;
	}

}
