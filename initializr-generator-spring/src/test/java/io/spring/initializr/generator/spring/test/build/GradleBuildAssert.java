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

package io.spring.initializr.generator.spring.test.build;

import org.assertj.core.api.AbstractStringAssert;

/**
 * Very simple assertions for the gradle build.
 *
 * @author Stephane Nicoll
 */
public class GradleBuildAssert extends AbstractStringAssert<GradleBuildAssert> {

	public GradleBuildAssert(String content) {
		super(content, GradleBuildAssert.class);
	}

	/**
	 * Assert {@code build.gradle} uses the specified {@code version}.
	 * @param version the version of the build
	 * @return this
	 */
	public GradleBuildAssert hasVersion(String version) {
		return contains("version = '" + version + "'");
	}

	/**
	 * Assert {@code build.gradle} uses an ext build script block declaring the Spring
	 * Boot plugin with the specified version.
	 * @param springBootVersion the spring boot version
	 * @return this
	 */
	public GradleBuildAssert hasSpringBootBuildScriptPlugin(String springBootVersion) {
		return contains("ext {")
				.contains("org.springframework.boot:spring-boot-gradle-plugin:"
						+ springBootVersion);
	}

	/**
	 * Assert {@code build.gradle} declares the Spring Boot plugin with the specified
	 * version.
	 * @param springBootVersion the spring boot version
	 * @return this
	 */
	public GradleBuildAssert hasSpringBootPlugin(String springBootVersion) {
		return contains(
				"id 'org.springframework.boot' version '" + springBootVersion + "'");
	}

	/**
	 * Assert {@code build.gradle} declares a source compatibility for the specified java
	 * version.
	 * @param javaVersion the java version
	 * @return this
	 */
	public GradleBuildAssert hasJavaVersion(String javaVersion) {
		return contains("sourceCompatibility = '" + javaVersion + "'");
	}

	/**
	 * Assert {@code build.gradle} declares the {@code repo.spring.io/snapshot}
	 * repository.
	 * @return this
	 */
	public GradleBuildAssert hasSnapshotRepository() {
		return contains("https://repo.spring.io/snapshot");
	}

	/**
	 * Assert {@code build.gradle} declares a repository with the specified url.
	 * @param url the url of the repository
	 * @return this
	 */
	public GradleBuildAssert hasRepository(String url) {
		return contains("maven { url '" + url + "' }");
	}

	/**
	 * Assert {@code build.gradle} contains only the specified properties.
	 * @param values the property value pairs
	 * @return this for method chaining.
	 */
	public GradleBuildAssert hasProperties(String... values) {
		StringBuilder builder = new StringBuilder(String.format("ext {%n"));
		if (values.length % 2 == 1) {
			throw new IllegalArgumentException(
					"Size must be even, it is a set of property=value pairs");
		}
		for (int i = 0; i < values.length; i += 2) {
			builder.append(
					String.format("\tset('%s', \"%s\")%n", values[i], values[i + 1]));
		}
		builder.append("}");
		return contains(builder.toString());
	}

}
