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

import java.util.Map;

import io.spring.initializr.generator.version.Version;

/**
 * Dependency metadata for a given spring boot {@link Version}.
 *
 * @author Stephane Nicoll
 */
public class DependencyMetadata {

	final Version bootVersion;

	final Map<String, Dependency> dependencies;

	final Map<String, Repository> repositories;

	final Map<String, BillOfMaterials> boms;

	public DependencyMetadata() {
		this(null, null, null, null);
	}

	public DependencyMetadata(Version bootVersion, Map<String, Dependency> dependencies,
			Map<String, Repository> repositories, Map<String, BillOfMaterials> boms) {
		this.bootVersion = bootVersion;
		this.dependencies = dependencies;
		this.repositories = repositories;
		this.boms = boms;
	}

	public Version getBootVersion() {
		return this.bootVersion;
	}

	public Map<String, Dependency> getDependencies() {
		return this.dependencies;
	}

	public Map<String, Repository> getRepositories() {
		return this.repositories;
	}

	public Map<String, BillOfMaterials> getBoms() {
		return this.boms;
	}

}
