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

package io.spring.initializr.generator;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.Assert;

/**
 * Resolve {@link ProjectRequest} instances, honouring callback hook points.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestResolver {

	private final List<ProjectRequestPostProcessor> postProcessors;

	public ProjectRequestResolver(List<ProjectRequestPostProcessor> postProcessors) {
		this.postProcessors = new ArrayList<>(postProcessors);
	}

	public ProjectRequest resolve(ProjectRequest request, InitializrMetadata metadata) {
		Assert.notNull(request, "Request must not be null");
		applyPostProcessBeforeResolution(request, metadata);
		request.resolve(metadata);
		applyPostProcessAfterResolution(request, metadata);
		return request;
	}

	private void applyPostProcessBeforeResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : this.postProcessors) {
			processor.postProcessBeforeResolution(request, metadata);
		}
	}

	private void applyPostProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : this.postProcessors) {
			processor.postProcessAfterResolution(request, metadata);
		}
	}

}
