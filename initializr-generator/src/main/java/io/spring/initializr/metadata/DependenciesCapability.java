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

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.initializr.util.VersionParser;

/**
 * A {@link ServiceCapability} listing the available dependencies defined as a
 * {@link ServiceCapabilityType#HIERARCHICAL_MULTI_SELECT} capability.
 *
 * @author Stephane Nicoll
 */
public class DependenciesCapability extends ServiceCapability<List<DependencyGroup>> {

	final List<DependencyGroup> content = new ArrayList<>();

	@JsonIgnore
	private final Map<String, Dependency> indexedDependencies = new LinkedHashMap<>();

	public DependenciesCapability() {
		super("dependencies", ServiceCapabilityType.HIERARCHICAL_MULTI_SELECT,
				"Project dependencies", "dependency identifiers (comma-separated)");
	}

	@Override
	public List<DependencyGroup> getContent() {
		return content;
	}

	/**
	 * Return the {@link Dependency} with the specified id or {@code null} if no such
	 * dependency exists.
	 */
	public Dependency get(String id) {
		return indexedDependencies.get(id);
	}

	/**
	 * Return all dependencies as a flat collection
	 */
	public Collection<Dependency> getAll() {
		return Collections.unmodifiableCollection(indexedDependencies.values());
	}

	public void validate() {
		index();
	}

	public void updateVersionRange(VersionParser versionParser) {
		indexedDependencies.values().forEach(it -> it.updateVersionRanges(versionParser));
	}

	@Override
	public void merge(List<DependencyGroup> otherContent) {
		otherContent.forEach(group -> {
			if (content.stream().noneMatch(it -> group.getName() != null
					&& group.getName().equals(it.getName()))) {
				content.add(group);
			}
		});
		index();
	}

	private void index() {
		indexedDependencies.clear();
		content.forEach(group -> group.content.forEach(dependency -> {
			// Apply defaults
			if (dependency.getVersionRange() == null
					&& group.getVersionRange() != null) {
				dependency.setVersionRange(group.getVersionRange());
			}
			if (dependency.getBom() == null && group.getBom() != null) {
				dependency.setBom(group.getBom());
			}
			if (dependency.getRepository() == null && group.getRepository() != null) {
				dependency.setRepository(group.getRepository());
			}

			dependency.resolve();
			indexDependency(dependency.getId(), dependency);
			for (String alias : dependency.getAliases()) {
				indexDependency(alias, dependency);
			}
		}));
	}

	private void indexDependency(String id, Dependency dependency) {
		Dependency existing = indexedDependencies.get(id);
		if (existing != null) {
			throw new IllegalArgumentException(
					"Could not register " + dependency + " another dependency "
							+ "has also the '" + id + "' id " + existing);
		}
		indexedDependencies.put(id, dependency);
	}

}
