/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.spring.initializr.metadata.Link;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

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
		Map<@Nullable String, List<Link>> byRel = new LinkedHashMap<>();
		links.forEach((it) -> byRel.computeIfAbsent(it.getRel(), (k) -> new ArrayList<>()).add(it));
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
