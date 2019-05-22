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

package io.spring.initializr.generator.buildsystem;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import io.spring.initializr.generator.version.VersionReference;

import org.springframework.util.Assert;

/**
 * A dependency to be declared in a project's build configuration.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class Dependency {

	private final String groupId;

	private final String artifactId;

	private final VersionReference version;

	private final DependencyScope scope;

	private final String type;

	private final Set<Exclusion> exclusions;

	public Dependency(String groupId, String artifactId) {
		this(groupId, artifactId, DependencyScope.COMPILE);
	}

	public Dependency(String groupId, String artifactId, DependencyScope scope) {
		this(groupId, artifactId, null, scope);
	}

	public Dependency(String groupId, String artifactId, VersionReference version,
			DependencyScope scope) {
		this(groupId, artifactId, version, scope, null);
	}

	public Dependency(String groupId, String artifactId, VersionReference version,
			DependencyScope scope, String type) {
		this(groupId, artifactId, version, scope, type, null);
	}

	public Dependency(String groupId, String artifactId, VersionReference version,
			DependencyScope scope, String type, Set<Exclusion> exclusions) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.scope = scope;
		this.type = type;
		this.exclusions = (exclusions != null) ? exclusions : Collections.emptySet();
	}

	/**
	 * The group ID of the dependency.
	 * @return the group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * The artifact ID of the dependency.
	 * @return the artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * The {@link VersionReference} of the dependency. May be {@code null} for a
	 * dependency whose version is expected to be provided by dependency management.
	 * @return the version reference or {@code null}
	 */
	public VersionReference getVersion() {
		return this.version;
	}

	/**
	 * The {@link DependencyScope scope} of the dependency.
	 * @return the scope
	 */
	public DependencyScope getScope() {
		return this.scope;
	}

	/**
	 * The type of the dependency. Can be {@code null} to indicate that the default type
	 * should be used (i.e. {@code jar}).
	 * @return the type or {@code null}
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * The {@link Exclusion exclusions} to apply.
	 * @return the exclusions to apply
	 */
	public Set<Exclusion> getExclusions() {
		return this.exclusions;
	}

	/**
	 * Define the reference to a transitive dependency to exclude.
	 */
	public static final class Exclusion {

		private final String groupId;

		private final String artifactId;

		public Exclusion(String groupId, String artifactId) {
			Assert.hasText(groupId, "GroupId must not be null");
			Assert.hasText(groupId, "ArtifactId must not be null");
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		public String getGroupId() {
			return this.groupId;
		}

		public String getArtifactId() {
			return this.artifactId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Exclusion exclusion = (Exclusion) o;
			return this.groupId.equals(exclusion.groupId)
					&& this.artifactId.equals(exclusion.artifactId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.groupId, this.artifactId);
		}

	}

}
