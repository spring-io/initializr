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

package io.spring.initializr.web.mapper;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.Repository;

/**
 * A {@link DependencyMetadataJsonMapper} handling the metadata format for v2.1.
 *
 * @author Stephane Nicoll
 */
public class DependencyMetadataV21JsonMapper implements DependencyMetadataJsonMapper {

	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

	@Override
	public String write(DependencyMetadata metadata) {
		ObjectNode json = nodeFactory.objectNode();
		json.put("bootVersion", metadata.getBootVersion().toString());
		json.set("dependencies",
				mapNode(metadata.getDependencies().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey,
								(entry) -> mapDependency(entry.getValue())))));
		json.set("repositories",
				mapNode(metadata.getRepositories().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey,
								(entry) -> mapRepository(entry.getValue())))));
		json.set("boms", mapNode(metadata.getBoms().entrySet().stream().collect(Collectors
				.toMap(Map.Entry::getKey, (entry) -> mapBom(entry.getValue())))));
		return json.toString();
	}

	private static JsonNode mapDependency(Dependency dep) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("groupId", dep.getGroupId());
		node.put("artifactId", dep.getArtifactId());
		if (dep.getVersion() != null) {
			node.put("version", dep.getVersion());
		}
		node.put("scope", dep.getScope());
		if (dep.getBom() != null) {
			node.put("bom", dep.getBom());
		}
		if (dep.getRepository() != null) {
			node.put("repository", dep.getRepository());
		}
		return node;
	}

	private static JsonNode mapRepository(Repository repo) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("name", repo.getName())
				.put("url", (repo.getUrl() != null ? repo.getUrl().toString() : null))
				.put("snapshotEnabled", repo.isSnapshotsEnabled());
		return node;
	}

	private static JsonNode mapBom(BillOfMaterials bom) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("groupId", bom.getGroupId());
		node.put("artifactId", bom.getArtifactId());
		if (bom.getVersion() != null) {
			node.put("version", bom.getVersion());
		}
		if (bom.getRepositories() != null) {
			ArrayNode array = nodeFactory.arrayNode();
			bom.getRepositories().forEach(array::add);
			node.set("repositories", array);
		}
		return node;
	}

	private static JsonNode mapNode(Map<String, JsonNode> content) {
		ObjectNode node = nodeFactory.objectNode();
		content.forEach(node::set);
		return node;
	}

}
