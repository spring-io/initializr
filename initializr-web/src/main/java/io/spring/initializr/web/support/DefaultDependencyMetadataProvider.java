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

import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Repository;

import org.springframework.cache.annotation.Cacheable;

/**
 * A default {@link DependencyMetadataProvider} implementation.
 *
 * @author Stephane Nicoll
 */
public class DefaultDependencyMetadataProvider implements DependencyMetadataProvider {

	@Override
	@Cacheable(cacheNames = "initializr.dependency-metadata", key = "#p1")
	public DependencyMetadata get(InitializrMetadata metadata, Version bootVersion) {
		Map<String, Dependency> dependencies = new LinkedHashMap<>();
		for (Dependency dependency : metadata.getDependencies().getAll()) {
			if (dependency.match(bootVersion)) {
				dependencies.put(dependency.getId(), dependency.resolve(bootVersion));
			}
		}

		Map<String, Repository> repositories = new LinkedHashMap<>();
		for (Dependency dependency : dependencies.values()) {
			if (dependency.getRepository() != null) {
				repositories.put(dependency.getRepository(), metadata.getConfiguration()
						.getEnv().getRepositories().get(dependency.getRepository()));
			}
		}

		Map<String, BillOfMaterials> boms = new LinkedHashMap<>();
		for (Dependency dependency : dependencies.values()) {
			if (dependency.getBom() != null) {
				boms.put(dependency.getBom(), metadata.getConfiguration().getEnv()
						.getBoms().get(dependency.getBom()).resolve(bootVersion));
			}
		}
		// Each resolved bom may require additional repositories
		for (BillOfMaterials bom : boms.values()) {
			for (String id : bom.getRepositories()) {
				repositories.put(id,
						metadata.getConfiguration().getEnv().getRepositories().get(id));
			}
		}

		return new DependencyMetadata(bootVersion, dependencies, repositories, boms);
	}

}
