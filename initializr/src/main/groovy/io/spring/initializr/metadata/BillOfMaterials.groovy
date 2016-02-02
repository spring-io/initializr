/*
 * Copyright 2012-2016 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.ToString
import io.spring.initializr.util.InvalidVersionException
import io.spring.initializr.util.Version
import io.spring.initializr.util.VersionRange

/**
 * Define a Bill Of Materials to be represented in the generated project
 * if a dependency refers to it.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ToString(ignoreNulls = true, excludes = 'mappings', includePackage = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
class BillOfMaterials {

	String groupId
	String artifactId

	/**
	 * The version of the BOM. Can be {@code null} if it is provided via a mapping.
	 */
	String version

	/**
	 * The BOM(s) that should be automatically included if this BOM is required. Can be
	 * {@code null} if it is provided via a mapping.
	 */
	List<String> additionalBoms = []

	/**
	 * The repositories that are required if this BOM is required. Can be {@code null} if
	 * it is provided via a mapping.
	 */
	List<String> repositories = []

	final List<Mapping> mappings = []

	void validate() {
		if (!version && !mappings) {
			throw new InvalidInitializrMetadataException("No version available for $this");
		}
		mappings.each {
			try {
				it.range = VersionRange.parse(it.versionRange)
			} catch (InvalidVersionException ex) {
				throw new InvalidInitializrMetadataException("Invalid version range $it.versionRange for $this", ex)
			}
		}
	}

	/**
	 * Resolve this instance according to the specified Spring Boot {@link Version}. Return
	 * a {@link BillOfMaterials} instance that holds the version, repositories and
	 * additional BOMs to use, if any.
	 */
	BillOfMaterials resolve(Version bootVersion) {
		if (!mappings) {
			return this
		}

		for (Mapping mapping : mappings) {
			if (mapping.range.match(bootVersion)) {
				def resolvedBom = new BillOfMaterials(groupId: groupId, artifactId: artifactId,
						version: mapping.version)
				resolvedBom.repositories += mapping.repositories ?: repositories
				resolvedBom.additionalBoms += mapping.additionalBoms ?: additionalBoms
				return resolvedBom
			}
		}
		throw new IllegalStateException("No suitable mapping was found for $this and version $bootVersion")
	}

	@ToString(ignoreNulls = true, includePackage = false)
	static class Mapping {

		String versionRange

		String version

		List<String> repositories = []

		List<String> additionalBoms = []

		private VersionRange range

	}

}
