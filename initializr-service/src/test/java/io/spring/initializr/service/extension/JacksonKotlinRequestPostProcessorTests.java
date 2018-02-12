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
import io.spring.initializr.metadata.Dependency;
import org.junit.Test;

/**
 * Tests for {@link JacksonKotlinRequestPostProcessor}.
 *
 * @author Sebastien Deleuze
 */
public class JacksonKotlinRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void jacksonModuleKotlinIsAdded() {
		ProjectRequest request = createProjectRequest("webflux");
		request.setBootVersion("2.0.0.M2");
		request.setLanguage("kotlin");
		Dependency jacksonKotlinModuleTest = Dependency.withId(
				"jackson-module-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin");
		generateMavenPom(request)
				.hasDependency(jacksonKotlinModuleTest)
				.hasDependenciesCount(6);
	}

	@Test
	public void jacksonModuleKotlinIsNotAddedWithoutKotlin() {
		ProjectRequest request = createProjectRequest("webflux");
		request.setBootVersion("2.0.0.M2");
		generateMavenPom(request)
				.hasDependenciesCount(3);
	}

	@Test
	public void jacksonModuleKotlinIsNotAddedWithoutJsonFacet() {
		ProjectRequest request = createProjectRequest("batch");
		request.setBootVersion("2.0.0.M2");
		request.setLanguage("kotlin");
		generateMavenPom(request)
				.hasDependenciesCount(5);
	}

}
