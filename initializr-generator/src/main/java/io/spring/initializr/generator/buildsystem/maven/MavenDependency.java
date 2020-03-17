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

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.Dependency;

/**
 * Maven-specific {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
public class MavenDependency extends Dependency {

	protected final boolean optional;

	protected MavenDependency(Builder builder) {
		super(builder);
		this.optional = builder.optional;
	}

	/**
	 * Initialize a new dependency {@link Builder} with the specified coordinates.
	 * @param groupId the group ID of the dependency
	 * @param artifactId the artifact ID of the dependency
	 * @return a new builder
	 */
	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

	/**
	 * Initialize a new dependency {@link Builder} with the state of the specified
	 * {@link Dependency}.
	 * @param dependency the dependency to use to initialize the builder
	 * @return a new builder initialized with the same state as the {@code dependency}
	 */
	public static Builder from(Dependency dependency) {
		return new Builder(dependency.getGroupId(), dependency.getArtifactId()).initialize(dependency);
	}

	/**
	 * Return whether this dependency is {@code optional}.
	 * @return {@code true} if the dependency is optional
	 */
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Builder for a Maven dependency.
	 *
	 * @see MavenDependency#withCoordinates(String, String)
	 */
	public static class Builder extends Dependency.Builder<Builder> {

		private boolean optional;

		protected Builder(String groupId, String artifactId) {
			super(groupId, artifactId);
		}

		/**
		 * Specify if the dependency is {@code optional}.
		 * @param optional whether the dependency is optional
		 * @return this for method chaining
		 */
		public Builder optional(boolean optional) {
			this.optional = optional;
			return self();
		}

		@Override
		protected Builder initialize(Dependency dependency) {
			super.initialize(dependency);
			if (dependency instanceof MavenDependency) {
				optional(((MavenDependency) dependency).isOptional());
			}
			return self();
		}

		@Override
		public MavenDependency build() {
			return new MavenDependency(this);
		}

	}

}
