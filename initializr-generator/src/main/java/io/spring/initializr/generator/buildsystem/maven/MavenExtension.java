/*
 * Copyright 2012-2023 the original author or authors.
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

/**
 * A build extension in a @{@link MavenBuild}.
 *
 * @author Niklas Herder
 * @author Stephane Nicoll
 */
public class MavenExtension {

	private final String groupId;

	private final String artifactId;

	private final String version;

	protected MavenExtension(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
	}

	/**
	 * Return the group ID of the extension.
	 * @return the group id
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * Return the artifact ID of the extension.
	 * @return the artifact id
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Return the version of the extension.
	 * @return the artifact id
	 */
	public String getVersion() {
		return this.version;
	}

	public static class Builder {

		private final String groupId;

		private final String artifactId;

		private String version;

		protected Builder(String groupId, String artifactId) {
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		/**
		 * Set the version of the extension.
		 * @param version the version of the extension
		 * @return this for method chaining
		 */
		public Builder version(String version) {
			this.version = version;
			return this;
		}

		/**
		 * Build a {@link MavenExtension} with the current state of this builder.
		 * @return a {@link MavenExtension}
		 */
		public MavenExtension build() {
			return new MavenExtension(this);
		}

	}

}
