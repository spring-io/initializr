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

package io.spring.initializr.web.mapper

import groovy.json.JsonBuilder
import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.DependencyMetadata
import io.spring.initializr.metadata.Repository

/**
 * A {@link DependencyMetadataJsonMapper} handling the meta-data format for v2.1.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class DependencyMetadataV21JsonMapper implements DependencyMetadataJsonMapper {

	@Override
	String write(DependencyMetadata metadata) {
		JsonBuilder json = new JsonBuilder()
		json {
			bootVersion metadata.bootVersion.toString()
			dependencies metadata.dependencies.collectEntries { id, d ->
				[id, mapDependency(d)]
			}
			repositories metadata.repositories.collectEntries { id, r -> [id, mapRepository(r)] }
			boms metadata.boms.collectEntries { id, b -> [id, mapBom(b)] }
		}
		json.toString()
	}

	private static mapDependency(Dependency dep) {
		def result = [:]
		result.groupId = dep.groupId
		result.artifactId = dep.artifactId
		if (dep.version) {
			result.version = dep.version
		}
		result.scope = dep.scope
		if (dep.bom) {
			result.bom = dep.bom
		}
		if (dep.repository) {
			result.repository = dep.repository
		}
		result
	}

	private static mapRepository(Repository repo) {
		def result = [:]
		result.name = repo.name
		result.url = repo.url
		result.snapshotEnabled = repo.snapshotsEnabled
		result
	}

	private static mapBom(BillOfMaterials bom) {
		def result = [:]
		result.groupId = bom.groupId
		result.artifactId = bom.artifactId
		if (bom.version) {
			result.version = bom.version
		}
		if (bom.repositories)  {
			result.repositories = bom.repositories
		}
		result
	}

}
