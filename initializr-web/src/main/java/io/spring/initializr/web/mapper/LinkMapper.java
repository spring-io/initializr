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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.Link;

/**
 * Generate a json representation for {@link Link}.
 *
 * @author Stephane Nicoll
 */
public final class LinkMapper {

	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

	private LinkMapper() {
	}

	/**
	 * Map the specified links to a json model. If several links share the same relation,
	 * they are grouped together.
	 * @param links the links to map
	 * @return a model for the specified links
	 */
	public static ObjectNode mapLinks(List<Link> links) {
		ObjectNode result = nodeFactory.objectNode();
		Map<String, List<Link>> byRel = new LinkedHashMap<>();
		links.forEach((it) -> byRel.computeIfAbsent(it.getRel(), (k) -> new ArrayList<>())
				.add(it));
		byRel.forEach((rel, l) -> {
			if (l.size() == 1) {
				ObjectNode root = JsonNodeFactory.instance.objectNode();
				mapLink(l.get(0), root);
				result.set(rel, root);
			}
			else {
				ArrayNode root = JsonNodeFactory.instance.arrayNode();
				l.forEach((link) -> {
					ObjectNode node = JsonNodeFactory.instance.objectNode();
					mapLink(link, node);
					root.add(node);
				});
				result.set(rel, root);
			}
		});
		return result;
	}

	private static void mapLink(Link link, ObjectNode node) {
		node.put("href", link.getHref());
		if (link.isTemplated()) {
			node.put("templated", true);
		}
		if (link.getDescription() != null) {
			node.put("title", link.getDescription());
		}
	}

}
