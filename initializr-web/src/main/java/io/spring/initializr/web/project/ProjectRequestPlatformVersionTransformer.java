/*
 * Copyright 2012-2020 the original author or authors.
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

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Strategy interface to transform the platform version of a {@link ProjectRequest}.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectRequestPlatformVersionTransformer {

    /**
     * Transform the platform version of a {@link ProjectRequest} if necessary.
     * @param platformVersion the candidate platform version
     * @param metadata the metadata instance to use
     * @return the platform version to use
     */
    Version transform(Version platformVersion, InitializrMetadata metadata);
}
