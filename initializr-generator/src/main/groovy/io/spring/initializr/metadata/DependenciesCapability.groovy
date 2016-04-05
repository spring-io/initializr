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

package io.spring.initializr.metadata

/**
 * A {@link ServiceCapability} listing the available dependencies defined as a
 * {@link ServiceCapabilityType#HIERARCHICAL_MULTI_SELECT} capability.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class DependenciesCapability extends ServiceCapability<List<DependencyGroup>> {

	final List<DependencyGroup> content = []

	private final Map<String, Dependency> indexedDependencies = [:]

	DependenciesCapability() {
		super('dependencies', ServiceCapabilityType.HIERARCHICAL_MULTI_SELECT,
				'Project dependencies', 'dependency identifiers (comma-separated)')
	}

	/**
	 * Return the {@link Dependency} with the specified id or {@code null} if
	 * no such dependency exists.
	 */
	Dependency get(String id) {
		indexedDependencies[id]
	}

	/**
	 * Return all dependencies as a flat collection
	 */
	Collection<Dependency> getAll() {
		Collections.unmodifiableCollection(indexedDependencies.values())
	}

	void validate() {
		index()
	}

	@Override
	void merge(List<DependencyGroup> otherContent) {
		otherContent.each { group ->
			if (!content.find { group.name.equals(it.name)}) {
				content << group
			}
		}
		index()
	}

	private void index() {
		indexedDependencies.clear()
		content.each { group ->
			group.content.each { dependency ->
				// Apply defaults
				if (!dependency.versionRange && group.versionRange) {
					dependency.versionRange = group.versionRange
				}
				if (!dependency.bom && group.bom) {
					dependency.bom = group.bom
				}
				if (!dependency.repository && group.repository) {
					dependency.repository = group.repository
				}

				dependency.resolve()
				indexDependency(dependency.id, dependency)
				for (String alias : dependency.aliases) {
					indexDependency(alias, dependency)
				}
			}
		}
	}

	private void indexDependency(String id, Dependency dependency) {
		def existing = indexedDependencies[id]
		if (existing) {
			throw new IllegalArgumentException("Could not register $dependency another dependency " +
					"has also the '$id' id $existing");
		}
		indexedDependencies[id] = dependency
	}

}
