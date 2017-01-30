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

package io.spring.initializr.web.mapper

import io.spring.initializr.metadata.Link

/**
 * Generate a json representation for {@link Link}
 *
 * @author Stephane Nicoll
 */
class LinkMapper {

	/**
	 * Map the specified links to a json model. If several links share
	 * the same relation, they are grouped together.
	 * @param links the links to map
	 * @return a model for the specified links
	 */
	static mapLinks(List<Link> links) {
		def result = [:]
		Map<String, List<Link>> byRel = new LinkedHashMap<>()
		links.each {
			def relLinks = byRel[it.rel]
			if (!relLinks) {
				relLinks = []
				byRel[it.rel] = relLinks
			}
			relLinks.add(it)
		}
		byRel.forEach { rel, l ->
			if (l.size() == 1) {
				def root = [:]
				mapLink(l[0], root)
				result[rel] = root
			} else {
				def root = []
				l.each {
					def model = [:]
					mapLink(it, model)
					root << model
				}
				result[rel] = root
			}
		}
		result
	}

	private static mapLink(Link link, def model) {
		model.href = link.href
		if (link.templated) {
			model.templated = true
		}
		if (link.description) {
			model.title = link.description
		}
	}

}
