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
 * Tests for {@link SpringCloudFunctionRequestPostProcessor}.
 *
 * @author Dave Syer
 */
public class SpringCloudFunctionRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void functionOnly() {
		ProjectRequest request = createProjectRequest("cloud-function");
		generateMavenPom(request).hasDependency(getDependency("cloud-function"))
				.hasSpringBootStarterTest().hasDependenciesCount(2);
	}

	@Test
	public void springCloudStreamWithRabbit() {
		ProjectRequest request = createProjectRequest("cloud-stream", "amqp",
				"cloud-function");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudFunctionRequestPostProcessor.SCS_ADAPTER)
				.hasDependenciesCount(6);
	}

	@Test
	public void reactiveSpringCloudStreamWithKafka() {
		ProjectRequest request = createProjectRequest("reactive-cloud-stream", "kafka",
				"cloud-function");
		generateMavenPom(request).hasDependency(getDependency("reactive-cloud-stream"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(SpringCloudFunctionRequestPostProcessor.SCS_ADAPTER)
				.hasDependenciesCount(6);
	}

	@Test
	public void web() {
		ProjectRequest request = createProjectRequest("web", "cloud-function");
		generateMavenPom(request).hasDependency(getDependency("web"))
				.hasDependency(SpringCloudFunctionRequestPostProcessor.WEB_ADAPTER)
				.hasDependenciesCount(3);
	}

}
