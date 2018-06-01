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
 * Tests for {@link JacksonKotlinRequestPostProcessor}.
 *
 * @author Sebastien Deleuze
 * @author Stephane Nicoll
 */
public class JacksonKotlinRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void jacksonModuleKotlinIsAdded() {
		ProjectRequest request = createProjectRequest("webflux");
		request.setBootVersion("2.0.0.M2");
		request.setLanguage("kotlin");
		generateMavenPom(request).hasSpringBootStarterDependency("webflux")
				.hasDependency(JacksonKotlinRequestPostProcessor.JACKSON_KOTLIN)
				.hasSpringBootStarterTest()
				.hasDependency(ReactorTestRequestPostProcessor.REACTOR_TEST)
				.hasDependency("org.jetbrains.kotlin", "kotlin-reflect")
				.hasDependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
				.hasDependenciesCount(6);
	}

	@Test
	public void jacksonModuleKotlinIsNotAddedWithoutKotlin() {
		ProjectRequest request = createProjectRequest("webflux");
		request.setBootVersion("2.0.0.M2");
		generateMavenPom(request).hasSpringBootStarterDependency("webflux")
				.hasSpringBootStarterTest()
				.hasDependency(ReactorTestRequestPostProcessor.REACTOR_TEST)
				.hasDependenciesCount(3);
	}

	@Test
	public void jacksonModuleKotlinIsNotAddedWithoutJsonFacet() {
		ProjectRequest request = createProjectRequest("actuator");
		request.setBootVersion("2.0.0.M2");
		request.setLanguage("kotlin");
		generateMavenPom(request).hasSpringBootStarterDependency("actuator")
				.hasSpringBootStarterTest()
				.hasDependency("org.jetbrains.kotlin", "kotlin-reflect")
				.hasDependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
				.hasDependenciesCount(4);
	}

}
