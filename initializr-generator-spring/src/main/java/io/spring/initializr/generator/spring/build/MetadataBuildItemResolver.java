/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A {@link BuildItemResolver} that uses the {@link InitializrMetadata} to resolve build
 * items.
 *
 * @author Stephane Nicoll
 */
public final class MetadataBuildItemResolver implements BuildItemResolver {

	private final InitializrMetadata metadata;

	public MetadataBuildItemResolver(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public Dependency resolveDependency(String id) {
		return MetadataBuildItemMapper
				.toDependency(this.metadata.getDependencies().get(id));
	}

	@Override
	public BillOfMaterials resolveBom(String id) {
		return MetadataBuildItemMapper
				.toBom(this.metadata.getConfiguration().getEnv().getBoms().get(id));
	}

	@Override
	public MavenRepository resolveRepository(String id) {
		if (id.equals(MavenRepository.MAVEN_CENTRAL.getId())) {
			return MavenRepository.MAVEN_CENTRAL;
		}
		return MetadataBuildItemMapper.toRepository(id,
				this.metadata.getConfiguration().getEnv().getRepositories().get(id));
	}

}
