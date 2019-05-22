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

import java.util.Arrays;
import java.util.LinkedHashSet;
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

	Dependency(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
		this.scope = builder.scope;
		this.type = builder.type;
		this.exclusions = new LinkedHashSet<>(builder.exclusions);
	}

	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
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
	 * Builder for a dependency.
	 *
	 * @see Dependency#withCoordinates(String, String)
	 */
	public static final class Builder {

		private String groupId;

		private String artifactId;

		private VersionReference version;

		private DependencyScope scope;

		private String type;

		private Set<Exclusion> exclusions = new LinkedHashSet<>();

		private Builder(String groupId, String artifactId) {
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		public Builder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}

		public Builder artifactId(String artifactId) {
			this.artifactId = artifactId;
			return this;
		}

		public Builder version(VersionReference version) {
			this.version = version;
			return this;
		}

		public Builder scope(DependencyScope scope) {
			this.scope = scope;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder exclusions(Exclusion... exclusions) {
			this.exclusions = new LinkedHashSet<>(Arrays.asList(exclusions));
			return this;
		}

		public Dependency build() {
			return new Dependency(this);
		}

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
