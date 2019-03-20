/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.initializr.generator.version.VersionParser;

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
		return this.content;
	}

	/**
	 * Return the {@link Dependency} with the specified id or {@code null} if no such
	 * dependency exists.
	 * @param id the ID of the dependency
	 * @return the dependency or {@code null}
	 */
	public Dependency get(String id) {
		return this.indexedDependencies.get(id);
	}

	/**
	 * Return all dependencies as a flat collection.
	 * @return all dependencies
	 */
	public Collection<Dependency> getAll() {
		return Collections.unmodifiableCollection(this.indexedDependencies.values()
				.stream().distinct().collect(Collectors.toList()));
	}

	public void validate() {
		index();
	}

	public void updateVersionRange(VersionParser versionParser) {
		this.indexedDependencies.values()
				.forEach((it) -> it.updateVersionRanges(versionParser));
	}

	@Override
	public void merge(List<DependencyGroup> otherContent) {
		otherContent.forEach((group) -> {
			if (this.content.stream().noneMatch((it) -> group.getName() != null
					&& group.getName().equals(it.getName()))) {
				this.content.add(group);
			}
		});
		index();
	}

	private void index() {
		this.indexedDependencies.clear();
		this.content.forEach((group) -> group.content.forEach((dependency) -> {
			// Apply defaults
			if (dependency.getVersionRange() == null && group.getVersionRange() != null) {
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
		Dependency existing = this.indexedDependencies.get(id);
		if (existing != null) {
			throw new IllegalArgumentException(
					"Could not register " + dependency + " another dependency "
							+ "has also the '" + id + "' id " + existing);
		}
		this.indexedDependencies.put(id, dependency);
	}

}
