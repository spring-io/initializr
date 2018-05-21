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

import static io.spring.initializr.service.extension.SpringCloudFunctionRequestPostProcessor.STREAM_ADAPTER;
import static io.spring.initializr.service.extension.SpringCloudFunctionRequestPostProcessor.WEB_ADAPTER;

/**
 * Tests for {@link SpringCloudFunctionRequestPostProcessor}.
 *
 * @author Dave Syer
 */
public class SpringCloudFunctionRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void springCloudStreamWithRabbit() {
		ProjectRequest request = createProjectRequest("cloud-stream", "amqp",
				"cloud-function");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("amqp")).hasDependency(STREAM_ADAPTER)
				.hasDependenciesCount(7);
	}

	@Test
	public void web() {
		ProjectRequest request = createProjectRequest("web", "cloud-function");
		generateMavenPom(request).hasDependency(getDependency("cloud-function"))
				.hasDependency(getDependency("web")).hasDependency(WEB_ADAPTER)
				.hasDependenciesCount(4);
	}

}
