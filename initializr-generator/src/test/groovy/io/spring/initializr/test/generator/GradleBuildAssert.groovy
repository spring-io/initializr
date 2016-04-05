/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.test.generator

import io.spring.initializr.generator.ProjectRequest

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Very simple assertions for the gradle build.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class GradleBuildAssert {

	private final String content

	GradleBuildAssert(String content) {
		this.content = content
	}

	/**
	 * Validate that this generated gradle build validates against its request.
	 */
	GradleBuildAssert validateProjectRequest(ProjectRequest request) {
		hasArtifactId(request.artifactId).hasVersion(request.version).
				hasBootVersion(request.bootVersion).hasJavaVersion(request.javaVersion)
	}

	GradleBuildAssert hasArtifactId(String artifactId) {
		contains("baseName = '$artifactId'")
	}

	GradleBuildAssert hasVersion(String version) {
		contains("version = '$version'")
	}

	GradleBuildAssert hasBootVersion(String bootVersion) {
		contains("springBootVersion = '$bootVersion'")
	}

	GradleBuildAssert hasJavaVersion(String javaVersion) {
		contains("sourceCompatibility = $javaVersion")
		contains("targetCompatibility = $javaVersion")
	}

	GradleBuildAssert hasSnapshotRepository() {
		contains('https://repo.spring.io/snapshot')
	}

	GradleBuildAssert hasRepository(String url) {
		contains("maven { url \"$url\" }")
	}

	GradleBuildAssert contains(String expression) {
		assertTrue "$expression has not been found in gradle build $content", content.contains(expression)
		this
	}

	GradleBuildAssert doesNotContain(String expression) {
		assertFalse "$expression is not expected in gradle build $content", content.contains(expression)
		this
	}
}
