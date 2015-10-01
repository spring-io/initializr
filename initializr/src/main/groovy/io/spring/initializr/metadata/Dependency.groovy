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

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.ToString
import io.spring.initializr.util.InvalidVersionException
import io.spring.initializr.util.VersionRange

/**
 * Meta-data for a dependency. Each dependency has a primary identifier and an
 * arbitrary number of {@code aliases}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ToString(ignoreNulls = true, includePackage = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
class Dependency extends MetadataElement {

	static final String SCOPE_COMPILE = 'compile'
	static final String SCOPE_RUNTIME = 'runtime'
	static final String SCOPE_PROVIDED = 'provided'
	static final String SCOPE_TEST = 'test'
	static final List<String> SCOPE_ALL = [
			SCOPE_COMPILE,
			SCOPE_RUNTIME,
			SCOPE_PROVIDED,
			SCOPE_TEST
	]

	List<String> aliases = []

	List<String> facets = []

	String groupId

	String artifactId

	String version

	String scope = SCOPE_COMPILE

	String description

	String versionRange

	String bom

	String repository

	int weight

	List<String> keywords = []

	void setScope(String scope) {
		if (!SCOPE_ALL.contains(scope)) {
			throw new InvalidInitializrMetadataException("Invalid scope $scope must be one of $SCOPE_ALL")
		}
		this.scope = scope
	}

	void setVersionRange(String versionRange) {
		this.versionRange = versionRange ? versionRange.trim() : null
	}

	/**
	 * Specify if the dependency has its coordinates set, i.e. {@code groupId}
	 * and {@code artifactId}.
	 */
	boolean hasCoordinates() {
		groupId && artifactId
	}

	/**
	 * Define this dependency as a standard spring boot starter with the specified name
	 * <p>If no name is specified, the root 'spring-boot-starter' is assumed.
	 */
	Dependency asSpringBootStarter(String name) {
		groupId = 'org.springframework.boot'
		artifactId = name ? 'spring-boot-starter-' + name : 'spring-boot-starter'
		if (name) {
			id = name
		}
		this
	}

	/**
	 * Validate the dependency and complete its state based on the
	 * available information.
	 */
	def resolve() {
		if (id == null) {
			if (!hasCoordinates()) {
				throw new InvalidInitializrMetadataException(
						'Invalid dependency, should have at least an id or a groupId/artifactId pair.')
			}
			generateId()
		} else if (!hasCoordinates()) {
			// Let's build the coordinates from the id
			def st = new StringTokenizer(id, ':')
			if (st.countTokens() == 1) { // assume spring-boot-starter
				asSpringBootStarter(id)
			} else if (st.countTokens() == 2 || st.countTokens() == 3) {
				groupId = st.nextToken()
				artifactId = st.nextToken()
				if (st.hasMoreTokens()) {
					version = st.nextToken()
				}
			} else {
				throw new InvalidInitializrMetadataException(
						"Invalid dependency, id should have the form groupId:artifactId[:version] but got $id")
			}
		}
		if (versionRange) {
			try {
				VersionRange.parse(versionRange)
			} catch (InvalidVersionException ex) {
				throw new InvalidInitializrMetadataException("Invalid version range '$versionRange' for " +
						"dependency with id '$id'")
			}
		}
	}

	/**
	 * Generate an id using the groupId and artifactId
	 */
	def generateId() {
		if (groupId == null || artifactId == null) {
			throw new IllegalArgumentException(
					"Could not generate id for $this: at least groupId and artifactId must be set.")
		}
		StringBuilder sb = new StringBuilder()
		sb.append(groupId).append(':').append(artifactId)
		id = sb.toString()
	}

}
