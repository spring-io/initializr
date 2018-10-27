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

package io.spring.initializr.web.ui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.Version;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UI specific controller providing dedicated endpoints for the Web UI.
 *
 * @author Stephane Nicoll
 */
@RestController
public class UiController {

	protected final InitializrMetadataProvider metadataProvider;

	public UiController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	@GetMapping(path = "/ui/dependencies", produces = "application/json")
	public ResponseEntity<String> dependencies(
			@RequestParam(required = false) String version) {
		List<DependencyGroup> dependencyGroups = this.metadataProvider.get()
				.getDependencies().getContent();
		List<DependencyItem> content = new ArrayList<>();
		Version requestedVersion = (StringUtils.isEmpty(version) ? null
				: Version.parse(version));
		dependencyGroups.forEach((group) -> group.getContent().forEach((dependency) -> {
			if (requestedVersion != null && dependency.getVersionRange() != null) {
				if (dependency.match(requestedVersion)) {
					content.add(new DependencyItem(group.getName(), dependency));
				}
			}
			else {
				content.add(new DependencyItem(group.getName(), dependency));
			}
		}));
		String json = writeDependencies(content);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
				.eTag(createUniqueId(json)).body(json);
	}

	private static String writeDependencies(List<DependencyItem> items) {
		ObjectNode json = JsonNodeFactory.instance.objectNode();
		ArrayNode maps = JsonNodeFactory.instance.arrayNode();
		items.forEach((dependency) -> maps.add(mapDependency(dependency)));
		json.set("dependencies", maps);
		return json.toString();
	}

	private static ObjectNode mapDependency(DependencyItem item) {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		Dependency dependency = item.dependency;
		node.put("id", dependency.getId());
		node.put("name", dependency.getName());
		node.put("group", item.group);
		if (dependency.getDescription() != null) {
			node.put("description", dependency.getDescription());
		}
		if (dependency.getWeight() > 0) {
			node.put("weight", dependency.getWeight());
		}
		if (!CollectionUtils.isEmpty(dependency.getKeywords())
				|| !CollectionUtils.isEmpty(dependency.getAliases())) {
			List<String> all = new ArrayList<>(dependency.getKeywords());
			all.addAll(dependency.getAliases());
			node.put("keywords", StringUtils.collectionToCommaDelimitedString(all));
		}
		return node;
	}

	private String createUniqueId(String content) {
		StringBuilder builder = new StringBuilder();
		DigestUtils.appendMd5DigestAsHex(content.getBytes(StandardCharsets.UTF_8),
				builder);
		return builder.toString();
	}

	private static class DependencyItem {

		private final String group;

		private final Dependency dependency;

		DependencyItem(String group, Dependency dependency) {
			this.group = group;
			this.dependency = dependency;
		}

	}

}
