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

package io.spring.initializr.web.project;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Convert a {@link ProjectRequest} to a {@link ProjectDescription}.
 *
 * @param <R> the concrete {@link ProjectRequest} type
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectRequestToDescriptionConverter<R extends ProjectRequest> {

	/**
	 * Validate and convert the specified {@link ProjectRequest} to a
	 * {@link ProjectDescription} used as the source of project generation.
	 * @param request the request to convert
	 * @param metadata the metadata instance to use
	 * @return a validated {@link ProjectDescription} to use to generate a project that
	 * matches the specified {@code request}
	 */
	ProjectDescription convert(R request, InitializrMetadata metadata);

}
