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

package io.spring.initializr.generator;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import io.spring.initializr.util.VersionProperty;

/**
 * Build properties associated to a project request.
 *
 * @author Stephane Nicoll
 */
public class BuildProperties {

	/**
	 * Maven-specific build properties, added to the regular {@code properties} element.
	 */
	private final TreeMap<String, Supplier<String>> maven = new TreeMap<>();

	/**
	 * Gradle-specific build properties, added to the {@code buildscript} section of the
	 * gradle build.
	 */
	private final TreeMap<String, Supplier<String>> gradle = new TreeMap<>();

	/**
	 * Version properties. Shared between the two build systems.
	 */
	private final TreeMap<VersionProperty, Supplier<String>> versions = new TreeMap<>();

	public Map<String, Supplier<String>> getMaven() {
		return this.maven;
	}

	public Map<String, Supplier<String>> getGradle() {
		return this.gradle;
	}

	public Map<VersionProperty, Supplier<String>> getVersions() {
		return this.versions;
	}

}
