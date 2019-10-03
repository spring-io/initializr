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

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;

/**
 * Gradle-specific {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
public class GradleDependency extends Dependency {

	private final String configuration;

	protected GradleDependency(Builder builder) {
		super(builder);
		this.configuration = builder.configuration;
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
	 * Return the configuration to use for the dependency. If not set, a default
	 * configuration is inferred from the {@linkplain #getScope() scope} of the
	 * dependency.
	 * @return the custom configuration name to use or {@code null}
	 */
	public String getConfiguration() {
		return this.configuration;
	}

	/**
	 * Builder for a Gradle dependency.
	 *
	 * @see GradleDependency#withCoordinates(String, String)
	 */
	public static class Builder extends Dependency.Builder<Builder> {

		private String configuration;

		protected Builder(String groupId, String artifactId) {
			super(groupId, artifactId);
		}

		/**
		 * Specify the configuration to use for the dependency. Overrides the
		 * {@linkplain DependencyScope scope}.
		 * @param configuration the name of the configuration to use
		 * @return this for method chaining
		 */
		public Builder configuration(String configuration) {
			this.configuration = configuration;
			return self();
		}

		@Override
		protected Builder initialize(Dependency dependency) {
			super.initialize(dependency);
			if (dependency instanceof GradleDependency) {
				configuration(((GradleDependency) dependency).getConfiguration());
			}
			return self();
		}

		@Override
		public GradleDependency build() {
			return new GradleDependency(this);
		}

	}

}
