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

import java.util.Optional;

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
class MapstructRequestPostProcessor implements ProjectRequestPostProcessor {

	private final Dependency mapstructJdk8;
	private final Dependency mapstructTestJdk8;
	private final Dependency mapstructProcessors;
	private final Dependency mapstructTestProcessors;

	public MapstructRequestPostProcessor() {
		this.mapstructJdk8 = Dependency.withId("mapstruct-jdk8", "org.mapstruct", "mapstruct-jdk8", "${mapstructVersion}");
		this.mapstructTestJdk8 = Dependency.withId("mapstruct-test-jdk8", "org.mapstruct", "mapstruct-jdk8", "${mapstructVersion}");
		this.mapstructProcessors = Dependency.withId("mapstruct-processor", "org.mapstruct", "mapstruct-processor", "${mapstructVersion}");
		this.mapstructTestProcessors = Dependency.withId("mapstruct-test-processor", "org.mapstruct", "mapstruct-processor", "${mapstructVersion}");

		this.mapstructJdk8.setScope(Dependency.SCOPE_COMPILE_ONLY);
		this.mapstructTestJdk8.setScope(Dependency.SCOPE_TEST_COMPILE_ONLY);
		this.mapstructProcessors.setScope(Dependency.SCOPE_COMPILE_ONLY);
		this.mapstructTestProcessors.setScope(Dependency.SCOPE_TEST_COMPILE_ONLY);
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		mapstruct(request).ifPresent(d -> {
			request.getBuildProperties().getGradle().put("mapstructVersion", d::getVersion);
			request.getResolvedDependencies().remove(d);
			request.getResolvedDependencies().add(mapstructJdk8);
			request.getResolvedDependencies().add(mapstructTestJdk8);
			request.getResolvedDependencies().add(mapstructProcessors);
			request.getResolvedDependencies().add(mapstructTestProcessors);
		});
	}

	private Optional<Dependency> mapstruct(ProjectRequest request) {
		return request.getResolvedDependencies().stream()
			.filter(d -> "mapstruct".equals(d.getId()))
			.findFirst();
	}
}
