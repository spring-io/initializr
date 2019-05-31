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
 * A {@link Dependency} with specific settings for the Maven build system.
 *
 * @author Stephane Nicoll
 */
public class MavenDependency extends Dependency {

	protected final boolean optional;

	protected MavenDependency(Builder builder) {
		super(builder);
		this.optional = builder.optional;
	}

	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

	public static Builder from(Dependency dependency) {
		return new Builder(dependency.getGroupId(), dependency.getArtifactId())
				.initialize(dependency);
	}

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
