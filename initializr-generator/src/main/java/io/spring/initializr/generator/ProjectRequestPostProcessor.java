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

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Project generation hook that allows for custom modification of {@link ProjectRequest}
 * instances, e.g. adding custom dependencies or forcing certain settings based on custom
 * logic.
 *
 * @author Stephane Nicoll
 */
public interface ProjectRequestPostProcessor {

	/**
	 * Apply this post processor to the given {@code ProjectRequest} <i>before</i> it gets
	 * resolved against the specified {@code InitializrMetadata}.
	 * <p>
	 * Consider using this hook to customize basic settings of the {@code request}; for
	 * more advanced logic (in particular with regards to dependencies), consider using
	 * {@code postProcessAfterResolution}.
	 * @param request an unresolved {@link ProjectRequest}
	 * @param metadata the metadata to use to resolve this request
	 * @see ProjectRequest#resolve(InitializrMetadata)
	 */
	default void postProcessBeforeResolution(ProjectRequest request,
			InitializrMetadata metadata) {
	}

	/**
	 * Apply this post processor to the given {@code ProjectRequest} <i>after</i> it has
	 * been resolved against the specified {@code InitializrMetadata}.
	 * <p>
	 * Dependencies, repositories, bills of materials, default properties and others
	 * aspects of the request will have been resolved prior to invocation. In particular,
	 * note that no further validation checks will be performed.
	 * @param request an resolved {@code ProjectRequest}
	 * @param metadata the metadata that were used to resolve this request
	 */
	default void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
	}

}
