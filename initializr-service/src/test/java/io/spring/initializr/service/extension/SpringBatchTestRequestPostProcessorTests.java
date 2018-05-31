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
import io.spring.initializr.metadata.Dependency;
import org.junit.Test;

/**
 * Tests for {@link SpringBatchTestRequestPostProcessor}.
 *
 * @author Tim Riemer
 */
public class SpringBatchTestRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void batchTestIsAddedWithBatch() {
		ProjectRequest request = createProjectRequest("batch");
		generateMavenPom(request).hasSpringBootStarterDependency("batch")
				.hasSpringBootStarterTest().hasDependency(springBatchTest())
				.hasDependenciesCount(3);
	}

	@Test
	public void batchTestIsNotAddedBefore13() {
		ProjectRequest request = createProjectRequest("batch");
		request.setBootVersion("1.2.7.RELEASE");
		generateMavenPom(request).hasSpringBootStarterDependency("batch")
				.hasSpringBootStarterTest().hasDependenciesCount(2);
	}

	@Test
	public void batchTestIsNotAddedWithoutSpringBatch() {
		ProjectRequest request = createProjectRequest("web");
		generateMavenPom(request).hasSpringBootStarterDependency("web")
				.hasSpringBootStarterTest().hasDependenciesCount(2);
	}

	private static Dependency springBatchTest() {
		Dependency dependency = Dependency.withId("spring-batch-test",
				"org.springframework.batch", "spring-batch-test");
		dependency.setScope(Dependency.SCOPE_TEST);
		return dependency;
	}

}
