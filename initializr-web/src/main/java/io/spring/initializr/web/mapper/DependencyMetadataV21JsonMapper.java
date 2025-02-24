/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.web.mapper;

import java.util.List;
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

import org.springframework.util.CollectionUtils;

/**
 * A {@link DependencyMetadataJsonMapper} handling the metadata format for v2.1.
 *
 * @author Stephane Nicoll
 */
public class DependencyMetadataV21JsonMapper implements DependencyMetadataJsonMapper {

	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

	@Override
	public String write(DependencyMetadata metadata) {
		ObjectNode parent = nodeFactory.objectNode();
		parent.put("bootVersion", metadata.getBootVersion().toString());
		parent.set("dependencies", mapDependencies(metadata.getDependencies()));
		parent.set("repositories", mapRepositories(metadata.getRepositories()));
		parent.set("boms", mapBoms(metadata.getBoms()));
		customizeParent(parent, metadata);
		return parent.toString();
	}

	/**
	 * Customizes the parent.
	 * @param parent the parent
	 * @param metadata the metadata
	 */
	protected void customizeParent(ObjectNode parent, DependencyMetadata metadata) {
	}

	private static ObjectNode mapDependency(Dependency dep) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("groupId", dep.getGroupId());
		node.put("artifactId", dep.getArtifactId());
		node.put("scope", dep.getScope());
		addIfNotNull(node, "version", dep.getVersion());
		addIfNotNull(node, "bom", dep.getBom());
		addIfNotNull(node, "repository", dep.getRepository());
		return node;
	}

	private static void addIfNotNull(ObjectNode node, String key, String value) {
		if (value != null) {
			node.put(key, value);
		}
	}

	private static JsonNode mapRepository(Repository repo) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("name", repo.getName())
			.put("url", (repo.getUrl() != null) ? repo.getUrl().toString() : null)
			.put("snapshotEnabled", repo.isSnapshotsEnabled());
		return node;
	}

	private static ObjectNode mapBom(BillOfMaterials bom) {
		ObjectNode node = nodeFactory.objectNode();
		node.put("groupId", bom.getGroupId());
		node.put("artifactId", bom.getArtifactId());
		addIfNotNull(node, "version", bom.getVersion());
		addArrayIfNotNull(node, bom.getRepositories());
		return node;
	}

	private static void addArrayIfNotNull(ObjectNode node, List<String> values) {
		if (!CollectionUtils.isEmpty(values)) {
			ArrayNode arrayNode = nodeFactory.arrayNode();
			values.forEach(arrayNode::add);
			node.set("repositories", arrayNode);
		}
	}

	private static ObjectNode mapNode(Map<String, JsonNode> content) {
		ObjectNode node = nodeFactory.objectNode();
		content.forEach(node::set);
		return node;
	}

	private ObjectNode mapDependencies(Map<String, Dependency> dependencies) {
		return mapNode(dependencies.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, (entry) -> mapDependency(entry.getValue()))));
	}

	private ObjectNode mapRepositories(Map<String, Repository> repositories) {
		return mapNode(repositories.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, (entry) -> mapRepository(entry.getValue()))));
	}

	private ObjectNode mapBoms(Map<String, BillOfMaterials> boms) {
		return mapNode(boms.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, (entry) -> mapBom(entry.getValue()))));
	}

}
