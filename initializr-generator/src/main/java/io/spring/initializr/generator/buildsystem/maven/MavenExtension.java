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

/**
 * An extension entry in a @{@link MavenBuild}.
 *
 * @author Niklas Herder
 */
public class MavenExtension {

	private final String groupId;

	private final String artifactId;

	private String version;

	protected MavenExtension(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

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

		public Builder version(String version) {
			this.version = version;
			return this;
		}

		public MavenExtension build() {
			return new MavenExtension(this);
		}

	}

}
