/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.web.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.Repository;
import org.json.JSONObject;

/**
 * A {@link DependencyMetadataJsonMapper} handling the metadata format for v2.1.
 *
 * @author Stephane Nicoll
 */
public class DependencyMetadataV21JsonMapper implements DependencyMetadataJsonMapper {

	@Override
	public String write(DependencyMetadata metadata) {
		JSONObject json = new JSONObject();
		json.put("bootVersion", metadata.getBootVersion().toString());
		json.put("dependencies",
				metadata.getDependencies().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey,
								entry -> mapDependency(entry.getValue()))));
		json.put("repositories",
				metadata.getRepositories().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey,
								entry -> mapRepository(entry.getValue()))));
		json.put("boms", metadata.getBoms().entrySet().stream().collect(Collectors
				.toMap(Map.Entry::getKey, entry -> mapBom(entry.getValue()))));
		return json.toString();
	}

	private static Map<String, Object> mapDependency(Dependency dep) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("groupId", dep.getGroupId());
		result.put("artifactId", dep.getArtifactId());
		if (dep.getVersion() != null) {
			result.put("version", dep.getVersion());
		}
		result.put("scope", dep.getScope());
		if (dep.getBom() != null) {
			result.put("bom", dep.getBom());
		}
		if (dep.getRepository() != null) {
			result.put("repository", dep.getRepository());
		}
		if(dep.getTopic() != null) {
			result.put("topic", dep.getTopic());
		}
		return result;
	}

	private static Map<String, Object> mapRepository(Repository repo) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("name", repo.getName());
		result.put("url", repo.getUrl());
		result.put("snapshotEnabled", repo.isSnapshotsEnabled());
		return result;
	}

	private static Map<String, Object> mapBom(BillOfMaterials bom) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("groupId", bom.getGroupId());
		result.put("artifactId", bom.getArtifactId());
		if (bom.getVersion() != null) {
			result.put("version", bom.getVersion());
		}
		if (bom.getRepositories() != null) {
			result.put("repositories", bom.getRepositories());
		}
		return result;
	}

}
