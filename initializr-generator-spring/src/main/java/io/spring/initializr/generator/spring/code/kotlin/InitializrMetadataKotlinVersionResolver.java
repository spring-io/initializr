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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.version.InitializrMetadataSpringBootVersionResolver;
import io.spring.initializr.generator.spring.version.SpringBootVersionResolver;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * {@link KotlinVersionResolver} that resolves the version from the
 * {@link InitializrMetadata}.
 *
 * @author Andy Wilkinson
 */
public class InitializrMetadataKotlinVersionResolver implements KotlinVersionResolver {

	private final InitializrMetadata metadata;

	private final SpringBootVersionResolver bootVersionResolver;

	public InitializrMetadataKotlinVersionResolver(InitializrMetadata metadata) {
		this(metadata, new InitializrMetadataSpringBootVersionResolver(metadata));
	}

	public InitializrMetadataKotlinVersionResolver(InitializrMetadata metadata,
			SpringBootVersionResolver bootVersionResolver) {
		this.metadata = metadata;
		this.bootVersionResolver = bootVersionResolver;
	}

	@Override
	public String resolveKotlinVersion(ProjectDescription description) {
		return this.metadata.getConfiguration().getEnv().getKotlin()
				.resolveKotlinVersion(this.bootVersionResolver.resolveSpringBootVersion(description));
	}

}
