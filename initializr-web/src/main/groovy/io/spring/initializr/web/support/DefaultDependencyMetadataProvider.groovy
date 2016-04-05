/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web.support

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.DependencyMetadata
import io.spring.initializr.metadata.DependencyMetadataProvider
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.Repository
import io.spring.initializr.util.Version

import org.springframework.cache.annotation.Cacheable

/**
 * A default {@link DependencyMetadataProvider} implementation.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class DefaultDependencyMetadataProvider implements DependencyMetadataProvider {

	@Override
	@Cacheable(cacheNames = "dependency-metadata", key = "#p1")
	DependencyMetadata get(InitializrMetadata metadata, Version bootVersion) {
		Map<String, Dependency> dependencies = [:]
		for (Dependency d : metadata.dependencies.getAll()) {
			if (d.match(bootVersion)) {
				dependencies[d.id] = d.resolve(bootVersion)
			}
		}

		Map<String, Repository> repositories = [:]
		for (Dependency d : dependencies.values()) {
			if (d.repository) {
				repositories[d.repository] = metadata.configuration.env.repositories[d.repository]
			}
		}

		Map<String, BillOfMaterials> boms = [:]
		for (Dependency d : dependencies.values()) {
			if (d.bom) {
				boms[d.bom] = metadata.configuration.env.boms.get(d.bom).resolve(bootVersion)
			}
		}
		// Each resolved bom may require additional repositories
		for (BillOfMaterials b : boms.values()) {
			for (String id : b.repositories) {
				repositories[id] = metadata.configuration.env.repositories[id]
			}
		}

		return new DependencyMetadata(bootVersion, dependencies, repositories, boms)
	}

}
