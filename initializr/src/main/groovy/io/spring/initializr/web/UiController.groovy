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

package io.spring.initializr.web

import groovy.json.JsonBuilder
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.util.Version
import io.spring.initializr.util.VersionRange

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * UI specific controller providing dedicated endpoints for the Web UI.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@RestController
class UiController {

	@Autowired
	protected InitializrMetadataProvider metadataProvider

	@RequestMapping(value = "/ui/dependencies", produces = ["application/json"])
	String dependencies(@RequestParam(required = false) String version) {
		def dependencyGroups = metadataProvider.get().dependencies.content
		def content = []
		Version v = version ? Version.parse(version) : null
		dependencyGroups.each { g ->
			g.content.each { d ->
				if (v && d.versionRange) {
					if (VersionRange.parse(d.versionRange).match(v)) {
						content << new DependencyItem(g.name, d)
					}
				} else {
					content << new DependencyItem(g.name, d)
				}
			}
		}
		writeDependencies(content)
	}

	private static String writeDependencies(List<DependencyItem> items) {
		JsonBuilder json = new JsonBuilder();
		json {
			dependencies items.collect { d ->
				mapDependency(d)
			}
		}
		json.toString()
	}

	private static mapDependency(DependencyItem item) {
		def result = [:]
		Dependency d = item.dependency
		result.id = d.id
		result.name = d.name
		result.group = item.group
		if (d.description) {
			result.description = d.description
		}
		if (d.weight) {
			result.weight = d.weight
		}
		if (d.keywords) {
			result.keywords = d.keywords.join(',')
		}
		result
	}

	private static class DependencyItem {
		private final String group
		private final Dependency dependency

		DependencyItem(String group, Dependency dependency) {
			this.group = group
			this.dependency = dependency
		}
	}

}
