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

import org.springframework.stereotype.Component;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A {@link ProjectRequestPostProcessor} that automatically adds
 * {@code spring-security-test} when Spring Security is selected.
 *
 * @author Stephane Nicoll
 */
@Component
class LombokTestRequestPostProcessor implements ProjectRequestPostProcessor {

	private final Dependency lombokTest;

	public LombokTestRequestPostProcessor() {
		this.lombokTest = Dependency.withId("lombok-test",
				"org.projectlombok", "lombok");
		this.lombokTest.setScope(Dependency.SCOPE_TEST_COMPILE_ONLY);
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		if (hasLombok(request)) {
			request.getResolvedDependencies().add(this.lombokTest);
		}
	}

	private boolean hasLombok(ProjectRequest request) {
		return request.getResolvedDependencies().stream()
				.anyMatch(d -> "lombok".equals(d.getId()));
	}
}
