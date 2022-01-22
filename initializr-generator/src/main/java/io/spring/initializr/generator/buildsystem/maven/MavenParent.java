/*
 * Copyright 2012-2022 the original author or authors.
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
 * The {@code <parent>} in a Maven pom.
 *
 * @author Andy Wilkinson
 */
public class MavenParent {

	private final String groupId;

	private final String artifactId;

	private final String version;

	private final String relativePath;

	MavenParent(String groupId, String artifactId, String version, String relativePath) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.relativePath = relativePath;
	}

	/**
	 * Return the group ID of the parent.
	 * @return the group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * Return the artifact ID of the parent.
	 * @return the artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Return the version of the parent.
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Return the relative path of this parent.
	 * @return the relative path of this parent or {@code null}.
	 */
	public String getRelativePath() {
		return this.relativePath;
	}

}
