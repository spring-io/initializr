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

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.stereotype.Component;

/**
 * Determine the appropriate Spring Cloud function dependency according to the messaging
 * and/or platform dependencies requested.
 *
 * @author Dave Syer
 */
@Component
class SpringCloudFunctionRequestPostProcessor
		extends AbstractProjectRequestPostProcessor {

	static final Dependency SCS_ADAPTER = Dependency.withId("cloud-function-stream",
			"org.springframework.cloud", "spring-cloud-function-stream");

	static final Dependency WEB_ADAPTER = Dependency.withId("cloud-function-web",
			"org.springframework.cloud", "spring-cloud-function-web");

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		Dependency cloudFunction = getDependency(request, "cloud-function");
		if (cloudFunction != null) {
			List<Dependency> swap = new ArrayList<>();
			if (hasDependency(request, "cloud-stream")
					|| hasDependency(request, "reactive-cloud-stream")) {
				swap.add(SCS_ADAPTER);
			}
			if (hasDependency(request, "web")) {
				swap.add(WEB_ADAPTER);
			}
			if (!swap.isEmpty()) {
				request.getResolvedDependencies().remove(cloudFunction);
				request.getResolvedDependencies().addAll(swap);
			}
		}
	}

}
