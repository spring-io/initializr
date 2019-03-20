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

package io.spring.initializr.metadata.support;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A {@link BuildItemResolver} that uses the {@link InitializrMetadata} to resolve build
 * items against a given platform {@link Version}.
 *
 * @author Stephane Nicoll
 */
public final class MetadataBuildItemResolver implements BuildItemResolver {

	private final InitializrMetadata metadata;

	private final Version platformVersion;

	/**
	 * Creates an instance for the specified {@link InitializrMetadata} and {@link Version
	 * platform version}.
	 * @param metadata the metadata to use
	 * @param platformVersion the platform version to consider
	 */
	public MetadataBuildItemResolver(InitializrMetadata metadata,
			Version platformVersion) {
		this.metadata = metadata;
		this.platformVersion = platformVersion;
	}

	@Override
	public Dependency resolveDependency(String id) {
		io.spring.initializr.metadata.Dependency dependency = this.metadata
				.getDependencies().get(id);
		if (dependency != null) {
			return MetadataBuildItemMapper
					.toDependency(dependency.resolve(this.platformVersion));
		}
		return null;
	}

	@Override
	public BillOfMaterials resolveBom(String id) {
		io.spring.initializr.metadata.BillOfMaterials bom = this.metadata
				.getConfiguration().getEnv().getBoms().get(id);
		if (bom != null) {
			return MetadataBuildItemMapper.toBom(bom.resolve(this.platformVersion));
		}
		return null;
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
