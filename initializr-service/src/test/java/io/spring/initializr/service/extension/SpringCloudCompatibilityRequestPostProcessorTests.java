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

import io.spring.initializr.generator.ProjectRequest;
import org.junit.Test;

/**
 * Tests for {@link SpringCloudCompatibilityRequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
public class SpringCloudCompatibilityRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void springBoot2MilestoneDowngradedWithSpringCloud() {
		ProjectRequest request = createProjectRequest("cloud-config-server");
		request.setBootVersion("2.0.0.M7");
		generateMavenPom(request)
				.hasSpringBootParent("2.0.0.M3");
	}

	@Test
	public void springBoot2SnapshotWithSpringCloudUsesLatest() {
		ProjectRequest request = createProjectRequest("cloud-config-server");
		request.setBootVersion("2.0.0.BUILD-SNAPSHOT");
		generateMavenPom(request)
				.hasSpringBootParent("2.0.0.BUILD-SNAPSHOT");
	}

	@Test
	public void springBoot2MilestoneNotDowngradedWithoutSpringCloud() {
		ProjectRequest request = createProjectRequest("web");
		request.setBootVersion("2.0.0.M7");
		generateMavenPom(request)
				.hasSpringBootParent("2.0.0.M7");
	}

	@Test
	public void onlySpringBoot2MilestoneHandled() {
		ProjectRequest request = createProjectRequest("cloud-config-server");
		request.setBootVersion("2.0.0.RC1");
		generateMavenPom(request)
				.hasSpringBootParent("2.0.0.RC1");
	}

	@Test
	public void onlySpringBoot2Handled() {
		ProjectRequest request = createProjectRequest("cloud-config-server");
		request.setBootVersion("1.5.7.RELEASE");
		generateMavenPom(request)
				.hasSpringBootParent("1.5.7.RELEASE");
	}

}