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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.spring.initializr.metadata.Link;

/**
 * Generate a json representation for {@link Link}
 *
 * @author Stephane Nicoll
 */
public class LinkMapper {

	/**
	 * Map the specified links to a json model. If several links share the same relation,
	 * they are grouped together.
	 * @param links the links to map
	 * @return a model for the specified links
	 */
	public static Map<String, Object> mapLinks(List<Link> links) {
		Map<String, Object> result = new LinkedHashMap<>();
		Map<String, List<Link>> byRel = new LinkedHashMap<>();
		links.forEach(it -> byRel.computeIfAbsent(it.getRel(),
				k -> new ArrayList<>()).add(it));
		byRel.forEach((rel, l) -> {
			if (l.size() == 1) {
				Map<String, Object> root = new LinkedHashMap<>();
				mapLink(l.get(0), root);
				result.put(rel, root);
			}
			else {
				List<Map<String, Object>> root = new ArrayList<>();
				l.forEach(link -> {
					Map<String, Object> model = new LinkedHashMap<>();
					mapLink(link, model);
					root.add(model);
				});
				result.put(rel, root);
			}
		});
		return result;
	}

	private static void mapLink(Link link, Map<String, Object> model) {
		model.put("href", link.getHref());
		if (link.isTemplated()) {
			model.put("templated", true);
		}
		if (link.getDescription() != null) {
			model.put("title", link.getDescription());
		}
	}

}
