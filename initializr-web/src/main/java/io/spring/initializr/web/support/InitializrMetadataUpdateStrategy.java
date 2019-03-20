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

package io.spring.initializr.web.support;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A strategy interface for updating the {@link InitializrMetadata metadata} on a running
 * instance.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface InitializrMetadataUpdateStrategy {

	/**
	 * Update the current {@link InitializrMetadata}.
	 * @param current the metadata to update
	 * @return the updated metadata, or {@code current} (never {@code null})
	 */
	InitializrMetadata update(InitializrMetadata current);

}
