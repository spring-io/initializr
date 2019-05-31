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

/**
 * A {@link Dependency} with specific settings for the Gradle build system.
 *
 * @author Stephane Nicoll
 */
public class GradleDependency extends Dependency {

	private final String configuration;

	public GradleDependency(Builder builder) {
		super(builder);
		this.configuration = builder.configuration;
	}

	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

	public static Builder from(Dependency dependency) {
		return new Builder(dependency.getGroupId(), dependency.getArtifactId())
				.initialize(dependency);
	}

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
