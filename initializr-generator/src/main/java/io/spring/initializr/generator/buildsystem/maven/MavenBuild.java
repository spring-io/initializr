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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;

/**
 * Maven build for a project.
 *
 * @author Andy Wilkinson
 */
public class MavenBuild extends Build {

	private MavenParent parent;

	private String name;

	private String description;

	private String sourceDirectory;

	private String testSourceDirectory;

	private final Map<String, String> properties = new TreeMap<>();

	private final Map<String, MavenPlugin> plugins = new LinkedHashMap<>();

	private String packaging;

	public MavenBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
	}

	public MavenBuild() {
		this(null);
	}

	public MavenParent parent(String groupId, String artifactId, String version) {
		this.parent = new MavenParent(groupId, artifactId, version);
		return this.parent;
	}

	public MavenParent getParent() {
		return this.parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	public String getSourceDirectory() {
		return this.sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getTestSourceDirectory() {
		return this.testSourceDirectory;
	}

	public void setTestSourceDirectory(String testSourceDirectory) {
		this.testSourceDirectory = testSourceDirectory;
	}

	public MavenPlugin plugin(String groupId, String artifactId) {
		return this.plugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(id) -> new MavenPlugin(groupId, artifactId));
	}

	public MavenPlugin plugin(String groupId, String artifactId, String version) {
		MavenPlugin mavenPlugin = this.plugins.computeIfAbsent(
				pluginKey(groupId, artifactId),
				(id) -> new MavenPlugin(groupId, artifactId));
		mavenPlugin.setVersion(version);
		return mavenPlugin;
	}

	private String pluginKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

	public List<MavenPlugin> getPlugins() {
		return Collections.unmodifiableList(new ArrayList<>(this.plugins.values()));
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getPackaging() {
		return this.packaging;
	}

}
