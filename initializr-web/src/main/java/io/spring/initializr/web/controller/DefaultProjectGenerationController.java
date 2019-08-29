/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.controller;

import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;

/**
 * A default {@link ProjectGenerationController} that uses a standard
 * {@link ProjectRequest} to map parameters of a project generation request.
 *
 * @author Stephane Nicoll
 */
public class DefaultProjectGenerationController extends ProjectGenerationController<ProjectRequest> {

	public DefaultProjectGenerationController(InitializrMetadataProvider metadataProvider,
			ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
		super(metadataProvider, projectGenerationInvoker);
	}

	@Override
	public ProjectRequest projectRequest(Map<String, String> headers) {
		WebProjectRequest request = new WebProjectRequest();
		request.getParameters().putAll(headers);
		request.initialize(getMetadata());
		return request;
	}

}
