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

package io.spring.initializr.metadata;

import io.spring.initializr.util.Version;

/**
 * Provide the {@link DependencyMetadata} for a given spring boot version.
 *
 * @author Stephane Nicoll
 */
public interface DependencyMetadataProvider {

	/**
	 * Return the dependency metadata to use for the specified {@code bootVersion}.
	 * @param metadata the intializr metadata
	 * @param bootVersion the Spring Boot version
	 * @return the dependency metadata
	 */
	DependencyMetadata get(InitializrMetadata metadata, Version bootVersion);

}
