/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.test.generator;

import io.spring.initializr.generator.ProjectRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Very simple assertions for the gradle build.
 *
 * @author Stephane Nicoll
 */
public class GradleBuildAssert {

	private final String content;

	public GradleBuildAssert(String content) {
		this.content = content;
	}

	/**
	 * Validate that this generated gradle build validates against its request.
	 * @param request the source request
	 * @return a gradle assert
	 */
	public GradleBuildAssert validateProjectRequest(ProjectRequest request) {
		return hasVersion(request.getVersion()).hasBootVersion(request.getBootVersion())
				.hasJavaVersion(request.getJavaVersion());
	}

	public GradleBuildAssert hasVersion(String version) {
		return contains("version = '" + version + "'");
	}

	public GradleBuildAssert hasBootVersion(String bootVersion) {
		return contains("springBootVersion = '" + bootVersion + "'");
	}

	public GradleBuildAssert hasJavaVersion(String javaVersion) {
		return contains("sourceCompatibility = " + javaVersion);
	}

	public GradleBuildAssert hasSnapshotRepository() {
		return contains("https://repo.spring.io/snapshot");
	}

	public GradleBuildAssert hasRepository(String url) {
		return contains("maven { url \"" + url + "\" }");
	}

	public GradleBuildAssert contains(String expression) {
		assertThat(this.content).contains(expression);
		return this;
	}

	public GradleBuildAssert doesNotContain(String expression) {
		assertThat(this.content).doesNotContain(expression);
		return this;
	}

	public String getGradleBuild() {
		return this.content;
	}

}
